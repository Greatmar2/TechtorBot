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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharth Verma on 24/4/16.
 */
public class Listing {

	/**
	 * The Children.
	 */
	@Expose
    public List<Post> children;
	/**
	 * The Modhash.
	 */
	@Expose
    public String modhash;
	/**
	 * The Before.
	 */
	@Expose
    public String before;
	/**
	 * The After.
	 */
	@Expose
    public String after;

	/**
	 * Instantiates a new Listing.
	 */
	public Listing() {
        this.children = new ArrayList<>();
    }
}
