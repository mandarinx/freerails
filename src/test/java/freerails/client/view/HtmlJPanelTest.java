/*
 * FreeRails
 * Copyright (C) 2000-2018 The FreeRails Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package freerails.client.view;

import junit.framework.TestCase;

import java.util.HashMap;

/**
 * Tests the populateTokens method on HtmlJPanel.
 *
 */
public class HtmlJPanelTest extends TestCase {

    /**
     *
     */
    public void testPopulateTokens() {
        String template = "test";
        HashMap<String, String> context = new HashMap<>();
        String output = HtmlJPanel.populateTokens(template, context);
        assertEquals(template, output);

        template = "Hello $name$, $question$";
        context.put("name", "Luke");
        context.put("question", "how are you?");

        String expectedOutput = "Hello Luke, how are you?";
        output = HtmlJPanel.populateTokens(template, context);
        assertEquals(expectedOutput, output);

        Object objectContext = new Object() {
            @SuppressWarnings("unused")
            public String name = "Luke";

            @SuppressWarnings("unused")
            public String question = "how are you?";
        };

        output = HtmlJPanel.populateTokens(template, objectContext);
        assertEquals(expectedOutput, output);
    }

    /**
     *
     */
    public void testPopulateTokens2() {
        String template = "Hey $a.name$ I would like you to meet $b.name$";
        String expectedOutput = "Hey Tom I would like you to meet Claire";

        Object objectContext = new Object() {
            @SuppressWarnings("unused")
            public Person a = new Person("Tom");

            @SuppressWarnings("unused")
            public Person b = new Person("Claire");
        };

        String output = HtmlJPanel.populateTokens(template, objectContext);
        assertEquals(expectedOutput, output);
    }

    /**
     *
     */
    public static class Person {

        /**
         *
         */
        public final String name;

        /**
         *
         * @param name
         */
        public Person(String name) {
            this.name = name;
        }
    }

}
