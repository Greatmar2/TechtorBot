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

package takeshi.db.model;

import java.util.EnumSet;

import takeshi.db.AbstractModel;

/**
 * The type O user.
 */
public class OUser extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id;
	/**
	 * The Discord id.
	 */
	public String discord_id;
	/**
	 * The Name.
	 */
	public String name;
	/**
	 * The Commands used.
	 */
	public int commandsUsed;
	/**
	 * The Banned.
	 */
	public int banned;
	/**
	 * The Last currency retrieval.
	 */
	public int lastCurrencyRetrieval = 0;
    private int permissionTotal;
    private EnumSet<PermissionNode> nodes;

	/**
	 * Instantiates a new O user.
	 */
	public OUser() {
        discord_id = "";
        id = 0;
        name = "";
        commandsUsed = 0;
        banned = 0;
        nodes = EnumSet.noneOf(PermissionNode.class);
        permissionTotal = 0;
    }

	/**
	 * Has permission boolean.
	 *
	 * @param node the node
	 * @return the boolean
	 */
	public boolean hasPermission(PermissionNode node) {
        return nodes.contains(node);
    }

	/**
	 * Gets encoded permissions.
	 *
	 * @return the encoded permissions
	 */
	public int getEncodedPermissions() {
        return permissionTotal;
    }

	/**
	 * Gets permission.
	 *
	 * @return the permission
	 */
	public EnumSet<PermissionNode> getPermission() {
        return nodes;
    }

	/**
	 * Sets permission.
	 *
	 * @param total the total
	 */
	public void setPermission(int total) {
        nodes = decode(total);
        permissionTotal = total;
    }

	/**
	 * Add permission boolean.
	 *
	 * @param node the node
	 * @return the boolean
	 */
	public boolean addPermission(PermissionNode node) {
        if (nodes.contains(node)) {
            return false;
        }
        nodes.add(node);
        permissionTotal = encode();
        return true;
    }

	/**
	 * Remove permission boolean.
	 *
	 * @param node the node
	 * @return the boolean
	 */
	public boolean removePermission(PermissionNode node) {
        if (!nodes.contains(node)) {
            return false;
        }
        nodes.remove(node);
        permissionTotal = encode();
        return true;
    }

    private EnumSet<PermissionNode> decode(int code) {
        PermissionNode[] values = PermissionNode.values();
        EnumSet<PermissionNode> result = EnumSet.noneOf(PermissionNode.class);
        while (code != 0) {
            int ordinal = Integer.numberOfTrailingZeros(code);
            code ^= Integer.lowestOneBit(code);
            result.add(values[ordinal]);
        }
        return result;
    }

    private int encode() {
        int ret = 0;
        for (PermissionNode val : nodes) {
            ret |= 1 << val.ordinal();
        }
        return ret;
    }

	/**
	 * The enum Permission node.
	 */
	public enum PermissionNode {
		/**
		 * The Import playlist.
		 */
		IMPORT_PLAYLIST("use youtube playlists"),
		/**
		 * The Ban tracks.
		 */
		BAN_TRACKS("ban tracks from the global playlist");
        private final String description;

        PermissionNode(String description) {

            this.description = description;
        }

		/**
		 * Gets description.
		 *
		 * @return the description
		 */
		public String getDescription() {
            return description;
        }
    }
}
