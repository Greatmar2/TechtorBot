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

package takeshi.modules.reddit.pojo;

import com.google.gson.annotations.Expose;

/**
 * Created by Siddharth Verma on 24/4/16.
 */
public class PostData {

	/**
	 * The Domain.
	 */
	@Expose
	public String domain;
	/**
	 * The Selftext.
	 */
	@Expose
	public String selftext;
	/**
	 * The Id.
	 */
	@Expose
	public String id;
	/**
	 * The Name.
	 */
	@Expose
	public String name;
	/**
	 * The Score.
	 */
	@Expose
	public Integer score;
	/**
	 * The Downs.
	 */
	@Expose
	public Integer downs;
	/**
	 * The Permalink.
	 */
	@Expose
	public String permalink;
	/**
	 * The Url.
	 */
	@Expose
	public String url;
	/**
	 * The Title.
	 */
	@Expose
	public String title;
	/**
	 * The Num comments.
	 */
	@Expose
	public String num_comments;
	/**
	 * The Ups.
	 */
	@Expose
	public Integer ups;
	/**
	 * The Post hint.
	 */
	@Expose
	public String post_hint;
	/**
	 * The Preview.
	 */
	@Expose
	public ImagePreview preview;
	/**
	 * The Is self.
	 */
	@Expose
	public boolean is_self;
	/**
	 * The Over 18.
	 */
	@Expose
	public boolean over_18;

	/**
	 * The Subreddit.
	 */
	@Expose
	public String subreddit;

	/**
	 * Gets domain.
	 *
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets domain.
	 *
	 * @param domain the domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Gets selftext.
	 *
	 * @return the selftext
	 */
	public String getSelftext() {
		return selftext;
	}

	/**
	 * Sets selftext.
	 *
	 * @param selftext the selftext
	 */
	public void setSelftext(String selftext) {
		this.selftext = selftext;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets score.
	 *
	 * @return the score
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * Sets score.
	 *
	 * @param score the score
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	/**
	 * Gets downs.
	 *
	 * @return the downs
	 */
	public Integer getDowns() {
		return downs;
	}

	/**
	 * Sets downs.
	 *
	 * @param downs the downs
	 */
	public void setDowns(Integer downs) {
		this.downs = downs;
	}

	/**
	 * Gets permalink.
	 *
	 * @return the permalink
	 */
	public String getPermalink() {
		return permalink;
	}

	/**
	 * Sets permalink.
	 *
	 * @param permalink the permalink
	 */
	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	/**
	 * Gets url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets url.
	 *
	 * @param url the url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets title.
	 *
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets num comments.
	 *
	 * @return the num comments
	 */
	public String getNum_comments() {
		return num_comments;
	}

	/**
	 * Sets num comments.
	 *
	 * @param num_comments the num comments
	 */
	public void setNum_comments(String num_comments) {
		this.num_comments = num_comments;
	}

	/**
	 * Gets ups.
	 *
	 * @return the ups
	 */
	public Integer getUps() {
		return ups;
	}

	/**
	 * Sets ups.
	 *
	 * @param ups the ups
	 */
	public void setUps(Integer ups) {
		this.ups = ups;
	}

	/**
	 * Gets post hint.
	 *
	 * @return the post hint
	 */
	public String getPost_hint() {
		return post_hint;
	}

	/**
	 * Sets post hint.
	 *
	 * @param post_hint the post hint
	 */
	public void setPost_hint(String post_hint) {
		this.post_hint = post_hint;
	}

	/**
	 * Gets preview.
	 *
	 * @return the preview
	 */
	public ImagePreview getPreview() {
		return preview;
	}

	/**
	 * Sets preview.
	 *
	 * @param preview the preview
	 */
	public void setPreview(ImagePreview preview) {
		this.preview = preview;
	}

}
