/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package org.activityinfo.endtoend.xpath.ext;

import org.activityinfo.endtoend.xpath.PredicateLiteral;
import org.activityinfo.endtoend.xpath.Predicate;
import org.activityinfo.endtoend.xpath.XPath;

public class HtmlXPath {

    public static Predicate ofClass(final String className) {
        return ofClasses(className);
    }

    public static Predicate ofClasses(String... classNames) {
        StringBuilder predicate = new StringBuilder("@class");
        for(String className : classNames) {
            predicate.append(XPath.format(" and contains(concat(' ', normalize-space(@class), ' '), '%s')",
                " " + className.trim() + " "));
        }
        return new PredicateLiteral(predicate.toString());
    }

}
