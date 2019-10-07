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
 * The type Tree.
 */
@Generated("org.jsonschema2pojo")
public class Tree {

    @SerializedName("sha")
    @Expose
    private String sha;
    @SerializedName("url")
    @Expose
    private String url;

	/**
	 * Gets sha.
	 *
	 * @return The sha
	 */
	public String getSha() {
        return sha;
    }

	/**
	 * Sets sha.
	 *
	 * @param sha The sha
	 */
	public void setSha(String sha) {
        this.sha = sha;
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

}
