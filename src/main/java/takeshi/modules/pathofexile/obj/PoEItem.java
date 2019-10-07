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

package takeshi.modules.pathofexile.obj;

import takeshi.modules.pathofexile.enums.Rarity;

/**
 * Created on 2-9-2016
 */
public class PoEItem {
	/**
	 * The Rarity.
	 */
	public Rarity rarity = Rarity.UNKNOWN;
	/**
	 * The Item level.
	 */
	public int itemLevel = 0;
	/**
	 * The Base.
	 */
	public String base = "";
	/**
	 * The Name.
	 */
	public String name = "";
	/**
	 * The Requirement level.
	 */
	public int requirementLevel = 0;
	/**
	 * The Requirement dex.
	 */
	public int requirementDex = 0;
	/**
	 * The Requirement str.
	 */
	public int requirementStr = 0;
	/**
	 * The Requirement int.
	 */
	public int requirementInt = 0;

    @Override
    public String toString() {
        return "PoEItem{" +
                "rarity=" + rarity +
                ", itemLevel=" + itemLevel +
                ", requirementLevel=" + requirementLevel +
                ", base='" + base + '\'' +
                ", name='" + name + '\'' +
                ", requirementDex=" + requirementDex +
                ", requirementStr=" + requirementStr +
                ", requirementInt=" + requirementInt +
                '}';
    }
}
