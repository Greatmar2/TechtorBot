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

package takeshi.command.meta;

import net.dv8tion.jda.api.entities.Guild;

/**
 * The type Pagination info.
 *
 * @param <E> the type parameter
 */
public class PaginationInfo<E> {

    private final int maxPage;
    private final Guild guild;
    private int currentPage = 0;
    private E extraData;

	/**
	 * Instantiates a new Pagination info.
	 *
	 * @param currentPage the current page
	 * @param maxPage     the max page
	 * @param guild       the guild
	 */
	public PaginationInfo(int currentPage, int maxPage, Guild guild) {
        this(currentPage, maxPage, guild, null);
    }

	/**
	 * Instantiates a new Pagination info.
	 *
	 * @param currentPage the current page
	 * @param maxPage     the max page
	 * @param guild       the guild
	 * @param extra       the extra
	 */
	public PaginationInfo(int currentPage, int maxPage, Guild guild, E extra) {

        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.guild = guild;
        this.extraData = extra;
    }

	/**
	 * Sets extra data.
	 *
	 * @param data the data
	 */
	public void setExtraData(E data) {
        extraData = data;
    }

	/**
	 * Gets extra.
	 *
	 * @return the extra
	 */
	public E getExtra() {
        return extraData;
    }

	/**
	 * Previous page boolean.
	 *
	 * @return the boolean
	 */
	public boolean previousPage() {
        if (currentPage > 1) {
            currentPage--;
            return true;
        }
        return false;
    }

	/**
	 * Next page boolean.
	 *
	 * @return the boolean
	 */
	public boolean nextPage() {
        if (currentPage < maxPage) {
            currentPage++;
            return true;
        }
        return false;
    }

	/**
	 * Gets max page.
	 *
	 * @return the max page
	 */
	public int getMaxPage() {
        return maxPage;
    }

	/**
	 * Gets current page.
	 *
	 * @return the current page
	 */
	public int getCurrentPage() {
        return currentPage;
    }

	/**
	 * Sets current page.
	 *
	 * @param currentPage the current page
	 */
	public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

	/**
	 * Gets guild.
	 *
	 * @return the guild
	 */
	public Guild getGuild() {
        return guild;
    }
}
