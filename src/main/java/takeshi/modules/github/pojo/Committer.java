/*
 * Copyright 2017 github.com/kaaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takeshi.modules.github.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * The type Committer.
 */
@Generated("org.jsonschema2pojo")
public class Committer {

    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;
    @SerializedName("gravatar_id")
    @Expose
    private String gravatarId;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    @SerializedName("followers_url")
    @Expose
    private String followersUrl;
    @SerializedName("following_url")
    @Expose
    private String followingUrl;
    @SerializedName("gists_url")
    @Expose
    private String gistsUrl;
    @SerializedName("starred_url")
    @Expose
    private String starredUrl;
    @SerializedName("subscriptions_url")
    @Expose
    private String subscriptionsUrl;
    @SerializedName("organizations_url")
    @Expose
    private String organizationsUrl;
    @SerializedName("repos_url")
    @Expose
    private String reposUrl;
    @SerializedName("events_url")
    @Expose
    private String eventsUrl;
    @SerializedName("received_events_url")
    @Expose
    private String receivedEventsUrl;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("site_admin")
    @Expose
    private Boolean siteAdmin;

	/**
	 * Gets login.
	 *
	 * @return The login
	 */
	public String getLogin() {
        return login;
    }

	/**
	 * Sets login.
	 *
	 * @param login The login
	 */
	public void setLogin(String login) {
        this.login = login;
    }

	/**
	 * Gets id.
	 *
	 * @return The id
	 */
	public Integer getId() {
        return id;
    }

	/**
	 * Sets id.
	 *
	 * @param id The id
	 */
	public void setId(Integer id) {
        this.id = id;
    }

	/**
	 * Gets avatar url.
	 *
	 * @return The avatarUrl
	 */
	public String getAvatarUrl() {
        return avatarUrl;
    }

	/**
	 * Sets avatar url.
	 *
	 * @param avatarUrl The avatar_url
	 */
	public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

	/**
	 * Gets gravatar id.
	 *
	 * @return The gravatarId
	 */
	public String getGravatarId() {
        return gravatarId;
    }

	/**
	 * Sets gravatar id.
	 *
	 * @param gravatarId The gravatar_id
	 */
	public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }

	/**
	 * Gets url.
	 *
	 * @return The url
	 */
	public String getUrl() {
        return url;
    }

	/**
	 * Sets url.
	 *
	 * @param url The url
	 */
	public void setUrl(String url) {
        this.url = url;
    }

	/**
	 * Gets html url.
	 *
	 * @return The htmlUrl
	 */
	public String getHtmlUrl() {
        return htmlUrl;
    }

	/**
	 * Sets html url.
	 *
	 * @param htmlUrl The html_url
	 */
	public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

	/**
	 * Gets followers url.
	 *
	 * @return The followersUrl
	 */
	public String getFollowersUrl() {
        return followersUrl;
    }

	/**
	 * Sets followers url.
	 *
	 * @param followersUrl The followers_url
	 */
	public void setFollowersUrl(String followersUrl) {
        this.followersUrl = followersUrl;
    }

	/**
	 * Gets following url.
	 *
	 * @return The followingUrl
	 */
	public String getFollowingUrl() {
        return followingUrl;
    }

	/**
	 * Sets following url.
	 *
	 * @param followingUrl The following_url
	 */
	public void setFollowingUrl(String followingUrl) {
        this.followingUrl = followingUrl;
    }

	/**
	 * Gets gists url.
	 *
	 * @return The gistsUrl
	 */
	public String getGistsUrl() {
        return gistsUrl;
    }

	/**
	 * Sets gists url.
	 *
	 * @param gistsUrl The gists_url
	 */
	public void setGistsUrl(String gistsUrl) {
        this.gistsUrl = gistsUrl;
    }

	/**
	 * Gets starred url.
	 *
	 * @return The starredUrl
	 */
	public String getStarredUrl() {
        return starredUrl;
    }

	/**
	 * Sets starred url.
	 *
	 * @param starredUrl The starred_url
	 */
	public void setStarredUrl(String starredUrl) {
        this.starredUrl = starredUrl;
    }

	/**
	 * Gets subscriptions url.
	 *
	 * @return The subscriptionsUrl
	 */
	public String getSubscriptionsUrl() {
        return subscriptionsUrl;
    }

	/**
	 * Sets subscriptions url.
	 *
	 * @param subscriptionsUrl The subscriptions_url
	 */
	public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

	/**
	 * Gets organizations url.
	 *
	 * @return The organizationsUrl
	 */
	public String getOrganizationsUrl() {
        return organizationsUrl;
    }

	/**
	 * Sets organizations url.
	 *
	 * @param organizationsUrl The organizations_url
	 */
	public void setOrganizationsUrl(String organizationsUrl) {
        this.organizationsUrl = organizationsUrl;
    }

	/**
	 * Gets repos url.
	 *
	 * @return The reposUrl
	 */
	public String getReposUrl() {
        return reposUrl;
    }

	/**
	 * Sets repos url.
	 *
	 * @param reposUrl The repos_url
	 */
	public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

	/**
	 * Gets events url.
	 *
	 * @return The eventsUrl
	 */
	public String getEventsUrl() {
        return eventsUrl;
    }

	/**
	 * Sets events url.
	 *
	 * @param eventsUrl The events_url
	 */
	public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

	/**
	 * Gets received events url.
	 *
	 * @return The receivedEventsUrl
	 */
	public String getReceivedEventsUrl() {
        return receivedEventsUrl;
    }

	/**
	 * Sets received events url.
	 *
	 * @param receivedEventsUrl The received_events_url
	 */
	public void setReceivedEventsUrl(String receivedEventsUrl) {
        this.receivedEventsUrl = receivedEventsUrl;
    }

	/**
	 * Gets type.
	 *
	 * @return The type
	 */
	public String getType() {
        return type;
    }

	/**
	 * Sets type.
	 *
	 * @param type The type
	 */
	public void setType(String type) {
        this.type = type;
    }

	/**
	 * Gets site admin.
	 *
	 * @return The siteAdmin
	 */
	public Boolean getSiteAdmin() {
        return siteAdmin;
    }

	/**
	 * Sets site admin.
	 *
	 * @param siteAdmin The site_admin
	 */
	public void setSiteAdmin(Boolean siteAdmin) {
        this.siteAdmin = siteAdmin;
    }

}
