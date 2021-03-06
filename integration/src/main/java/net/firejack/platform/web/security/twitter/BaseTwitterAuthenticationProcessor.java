/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.web.security.twitter;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTwitterAuthenticationProcessor implements IAuthenticationProcessor {

    private static final Logger logger = Logger.getLogger(BaseTwitterAuthenticationProcessor.class);

    public static final String TW_USER_ID = "tw_user_id";
    public static final String TW_SCREEN_NAME = "tw_screen_name";
    public static final String TW_US_SCREEN_NAME = "tw_us_screen_name";
    public static final String TW_US_NAME = "tw_us_name";
    public static final String TW_US_PROFILE_IMAGE_URL = "tw_us_profile_image_url";

    private String twitterAuthenticationEntryPointUrl;

    public String getTwitterAuthenticationEntryPointUrl() {
        return twitterAuthenticationEntryPointUrl;
    }

    public void setTwitterAuthenticationEntryPointUrl(String twitterAuthenticationEntryPointUrl) {
        this.twitterAuthenticationEntryPointUrl = twitterAuthenticationEntryPointUrl;
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        logger.info("Initialize Twitter Authentication Processor.");
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
        String requestPath = WebUtils.getRequestPath(request);
        return requestPath.equalsIgnoreCase(getTwitterAuthenticationEntryPointUrl());
    }

    protected AuthenticationToken getOpenFlameToken(Twitter twitterService, String browserIpAddress)
            throws TwitterException {
        try {
            Map<String, String> mappedAttributes = getTwitterUserInformation(twitterService);

            ServiceResponse<AuthenticationToken> response =
                    OPFEngine.AuthorityService.processTwitterIdSignIn(
                            twitterService.getId(), mappedAttributes, browserIpAddress);
            AuthenticationToken token;
            if (response == null) {
                throw new IllegalStateException("API Service response should not be null.");
            } else if (response.isSuccess()) {
                token = response.getItem();
            } else {
                logger.error("API Service response has failure status. Reason: " + response.getMessage());
                token = null;
            }
            return token;
        } catch (TwitterException e) {
            logger.error("Failed to obtain opf authentication token. Precondition failed, reason: " + e.getMessage());
            throw e;
        }
    }

    protected Map<String, String> getTwitterUserInformation(Twitter twitterService) throws TwitterException {
        Map<String, String> userInfo = new HashMap<String, String>();
        long id = twitterService.getId();
        userInfo.put(TW_USER_ID, String.valueOf(id));

        String screenName = twitterService.getScreenName();
        userInfo.put(TW_SCREEN_NAME, screenName);

        twitter4j.User user = twitterService.showUser(id);
        userInfo.put(TW_US_SCREEN_NAME, user.getScreenName());
        userInfo.put(TW_US_NAME, user.getName());
        userInfo.put(TW_US_PROFILE_IMAGE_URL, user.getProfileImageURL());

        return userInfo;
    }

    protected Twitter populateTwitterService() {
        if (!isEnable())
            throw new IllegalStateException("Service disabled.");

        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(getTwitterConsumerKey(), getTwitterConsumerSecret());
        return twitter;
    }

    private boolean isEnable() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(getEnableConfigLookup());
        return config == null ? false : Boolean.valueOf(config.getValue());
    }

    protected String getTwitterConsumerKey() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        String consumerKeyLookup = OpenFlameSecurityConstants.getPackageLookup() + ".twitter-consumer-key";
        logger.info("Twitter Consumer Key Lookup: [" + consumerKeyLookup + ']');
        Config config = configCacheManager.getConfig(consumerKeyLookup);
        String consumerKey = config != null ? config.getValue() : null;
        logger.info("Twitter Consumer Key: [" + consumerKey + ']');
        return consumerKey;
    }

    protected String getTwitterConsumerSecret() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        String consumerSecretLookup = OpenFlameSecurityConstants.getPackageLookup() + ".twitter-consumer-secret";
        logger.info("Twitter Consumer Secret Lookup: " + consumerSecretLookup);
        Config config = configCacheManager.getConfig(consumerSecretLookup);
        String consumerSecret = config != null ? config.getValue() : null;
        logger.info("Twitter Consumer Secret: [" + consumerSecret + ']');
        return consumerSecret;
    }

    protected String getEnableConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".twitter-enable";
    }

    @Override
    public void release() {
    }

}