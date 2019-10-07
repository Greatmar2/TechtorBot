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

package takeshi.role;

import java.awt.*;

/**
 * Created on 19-9-2016
 */
public class MemberShipRole {

    private final String name;
    private final Color color;
    private final long membershipTime;
    private final boolean hoisted;

	/**
	 * Instantiates a new Member ship role.
	 *
	 * @param name           the name
	 * @param color          the color
	 * @param membershipTime the membership time
	 */
	public MemberShipRole(String name, Color color, long membershipTime) {
        this.name = name;
        this.color = color;
        this.hoisted = false;
        this.membershipTime = membershipTime;
    }

	/**
	 * Instantiates a new Member ship role.
	 *
	 * @param name           the name
	 * @param color          the color
	 * @param membershipTime the membership time
	 * @param hoisted        the hoisted
	 */
	public MemberShipRole(String name, Color color, long membershipTime, boolean hoisted) {
        this.name = name;
        this.color = color;
        this.hoisted = hoisted;
        this.membershipTime = membershipTime;
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
	 * Gets color.
	 *
	 * @return the color
	 */
	public Color getColor() {
        return color;
    }

	/**
	 * Gets membership time.
	 *
	 * @return the membership time
	 */
	public long getMembershipTime() {
        return membershipTime;
    }

	/**
	 * Is hoisted boolean.
	 *
	 * @return the boolean
	 */
	public boolean isHoisted() {
        return hoisted;
    }
}
