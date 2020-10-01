/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
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

package matrix;

public class ThreePid implements _ThreePid {

    private String medium;
    private String address;

    public ThreePid(String medium, String address) {
        this.medium = medium;
        this.address = address;
    }

    @Override
    public String getMedium() {
        return medium;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreePid threePid = (ThreePid) o;

        if (!medium.equals(threePid.medium)) return false;
        return address.equals(threePid.address);
    }

    @Override
    public int hashCode() {
        int result = medium.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }

}
