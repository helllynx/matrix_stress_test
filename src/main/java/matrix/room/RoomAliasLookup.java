/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sàrl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package matrix.room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoomAliasLookup extends RoomAliasMapping implements _RoomAliasLookup {

    private List<String> servers;

    public RoomAliasLookup(String id, String alias, Collection<String> servers) {
        super(id, alias);
        this.servers = new ArrayList<>(servers);
    }

    @Override
    public List<String> getServers() {
        return servers;
    }

}
