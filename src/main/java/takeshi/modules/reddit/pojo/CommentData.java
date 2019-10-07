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
 * Created by Siddharth Verma on 5/5/16.
 */
public class CommentData {

	/**
	 * The Author.
	 */
	@Expose
    public String author;

	/**
	 * The Body.
	 */
	@Expose
    public String body;

	/**
	 * The Created.
	 */
	@Expose
    public Long created;

	/**
	 * The Created utc.
	 */
	@Expose
    public Long created_utc;

	/**
	 * The Subreddit.
	 */
	@Expose
    public String subreddit;

	/**
	 * The Score.
	 */
	@Expose
    public Integer score;

	/**
	 * The Id.
	 */
	@Expose
    public String id;

	/**
	 * The Replies.
	 */
	@Expose
    public InitialDataComment replies;
	/**
	 * The Is op.
	 */
	public boolean isOp;
}
