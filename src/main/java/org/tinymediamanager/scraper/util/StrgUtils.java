/*
 * Copyright 2012 - 2016 Manuel Laggner
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
package org.tinymediamanager.scraper.util;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class StrgUtils. This can be used for several String related tasks
 * 
 * @author Manuel Laggner, Myron Boyle
 * @since 1.0
 */
public class StrgUtils {
  private static final Map<Integer, Replacement> REPLACEMENTS          = buildReplacementMap();
  private static final String[]                  COMMON_TITLE_PREFIXES = buildCommonTitlePrefixes();

  /*
   * build a replacement map of characters, which are not handled right by the normalizer method
   */
  private static Map<Integer, Replacement> buildReplacementMap() {
    Map<Integer, Replacement> replacements = new HashMap<>();
    replacements.put(0xc6, new Replacement("AE", "Ae"));
    replacements.put(0xe6, new Replacement("ae"));
    replacements.put(0xd0, new Replacement("D"));
    replacements.put(0x111, new Replacement("d"));
    replacements.put(0xd8, new Replacement("O"));
    replacements.put(0xf8, new Replacement("o"));
    replacements.put(0x152, new Replacement("OE", "Oe"));
    replacements.put(0x153, new Replacement("oe"));
    replacements.put(0x166, new Replacement("T"));
    replacements.put(0x167, new Replacement("t"));
    replacements.put(0x141, new Replacement("L"));
    replacements.put(0x142, new Replacement("l"));
    return replacements;
  }

  private static String[] buildCommonTitlePrefixes() {
    // @formatter:off
    return new String[] { "A", "An", "The", // english
        "Der", "Die", "Das", "Ein", "Eine", "Eines", "Einer", "Einem", "Einen", // german
        "Le", "La", "Une", "Des", // french
        "El", "Los", "La", "Las", "Un", "Unos", "Una", "Unas" // spanish
    };
    // @formatter:on
  }

  /**
   * Removes the html.
   * 
   * @param html
   *          the html
   * @return the string
   */
  public static String removeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("<[^>]+>", "");
  }

  /**
   * Unquote.
   * 
   * @param str
   *          the str
   * @return the string
   */
  public static String unquote(String str) {
    if (str == null) {
      return null;
    }
    return str.replaceFirst("^\\\"(.*)\\\"$", "$1");
  }

  /**
   * Map to string.
   * 
   * @param map
   *          the map
   * @return the string
   */
  @SuppressWarnings("rawtypes")
  public static String mapToString(Map map) {
    if (map == null) {
      return "null";
    }
    if (map.size() == 0) {
      return "empty";
    }

    StringBuilder sb = new StringBuilder();
    for (Object o : map.entrySet()) {
      Map.Entry me = (Entry) o;
      sb.append(me.getKey()).append(": ").append(me.getValue()).append(",");
    }
    return sb.toString();
  }

  /**
   * Zero pad.
   * 
   * @param encodeString
   *          the encode string
   * @param padding
   *          the padding
   * @return the string
   */
  public static String zeroPad(String encodeString, int padding) {
    try {
      int v = Integer.parseInt(encodeString);
      String format = "%0" + padding + "d";
      return String.format(format, v);
    }
    catch (Exception e) {
      return encodeString;
    }
  }

  /**
   * gets regular expression based substring.
   * 
   * @param str
   *          the string to search
   * @param pattern
   *          the pattern to match; with ONE group bracket ()
   * @return the matched substring or empty string
   */
  public static String substr(String str, String pattern) {
    Pattern regex = Pattern.compile(pattern);
    Matcher m = regex.matcher(str);
    if (m.find()) {
      return m.group(1);
    }
    else {
      return "";
    }
  }

  /**
   * Parses the date.
   * 
   * @param dateAsString
   *          the date as string
   * @return the date
   * @throws ParseException
   *           the parse exception
   */
  public static Date parseDate(String dateAsString) throws ParseException {
    Date date = null;

    try {
      Pattern datePattern = Pattern.compile("([0-9]{2})[_\\.-]([0-9]{2})[_\\.-]([0-9]{4})");
      Matcher m = datePattern.matcher(dateAsString);
      if (m.find()) {
        date = new SimpleDateFormat("dd-MM-yyyy").parse(m.group(1) + "-" + m.group(2) + "-" + m.group(3));
      }
      else {
        datePattern = Pattern.compile("([0-9]{4})[_\\.-]([0-9]{2})[_\\.-]([0-9]{2})");
        m = datePattern.matcher(dateAsString);
        if (m.find()) {
          date = new SimpleDateFormat("yyyy-MM-dd").parse(m.group(1) + "-" + m.group(2) + "-" + m.group(3));
        }
      }
    }
    catch (Exception e) {
      // could not parse date
    }

    if (date == null) {
      throw new ParseException("could not parse date from: \"" + dateAsString + "\"", 0);
    }

    return date;
  }

  /**
   * Remove all duplicate whitespace characters and line terminators are replaced with a single space.
   * 
   * @param s
   *          a not null String
   * @return a string with unique whitespace.
   */
  public static String removeDuplicateWhitespace(String s) {
    StringBuilder result = new StringBuilder();
    int length = s.length();
    boolean isPreviousWhiteSpace = false;
    for (int i = 0; i < length; i++) {
      char c = s.charAt(i);
      boolean thisCharWhiteSpace = Character.isWhitespace(c);
      if (!(isPreviousWhiteSpace && thisCharWhiteSpace)) {
        result.append(c);
      }
      isPreviousWhiteSpace = thisCharWhiteSpace;
    }
    return result.toString();
  }

  /**
   * This method takes an input String and replaces all special characters like umlauts, accented or other letter with diacritical marks with their
   * basic ascii equivalents. Originally written by Jens Hausherr (https://github.com/jabbrwcky), modified by Manuel Laggner
   * 
   * @param input
   *          String to convert
   * @param replaceAllCapitalLetters
   *          <code>true</code> causes uppercase special chars that are replaced by more than one character to be replaced by all-uppercase
   *          replacements; <code>false</code> will cause only the initial character of the replacements to be in uppercase and all subsequent
   *          replacement characters will be in lowercase.
   * @return Input string reduced to ASCII-safe characters.
   */
  public static String convertToAscii(String input, boolean replaceAllCapitalLetters) {
    String result = null;
    if (null != input) {
      String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD);
      // https://stackoverflow.com/questions/9376621/folding-normalizing-ligatures-e-g-%C3%86-to-ae-using-corefoundation

      int len = normalized.length();
      result = processSpecialChars(normalized.toCharArray(), 0, len, replaceAllCapitalLetters);
    }

    return result;
  }

  /*
   * replace special characters
   */
  private static String processSpecialChars(char[] target, int offset, int len, boolean uppercase) {
    StringBuilder result = new StringBuilder();
    boolean skip = false;

    for (int i = 0; i < len; i++) {
      if (skip) {
        skip = false;
      }
      else {
        char c = target[i];
        if ((c > 0x20 && c < 0x40) || (c > 0x7a && c < 0xc0) || (c > 0x5a && c < 0x61) || (c > 0x79 && c < 0xc0) || c == 0xd7 || c == 0xf7) {
          result.append(c);
        }
        else if (Character.isDigit(c) || Character.isISOControl(c)) {
          result.append(c);
        }
        else if (Character.isWhitespace(c) || Character.isLetter(c)) {
          boolean isUpper = false;

          switch (c) {
            case '\u00df':
              result.append("ss");
              break;
            /* Handling of capital and lowercase umlauts */
            case 'A':
            case 'O':
            case 'U':
              isUpper = true;
            case 'a':
            case 'o':
            case 'u':
              result.append(c);
              if (i + 1 < target.length && target[i + 1] == 0x308) {
                result.append(isUpper && uppercase ? 'E' : 'e');
                skip = true;
              }
              break;
            default:
              Replacement rep = REPLACEMENTS.get(Integer.valueOf(c));
              if (rep != null) {
                result.append(uppercase ? rep.UPPER : rep.LOWER);
              }
              else {
                result.append(c);
              }
          }
        }
      }
    }
    return result.toString();
  }

  /**
   * Combination of replacements for upper- and lowercase mode.
   */
  private static class Replacement {
    private final String UPPER;
    private final String LOWER;

    Replacement(String ucReplacement, String lcReplacement) {
      UPPER = ucReplacement;
      LOWER = lcReplacement;
    }

    Replacement(String caseInsensitiveReplacement) {
      this(caseInsensitiveReplacement, caseInsensitiveReplacement);
    }
  }

  /**
   * Returns the common name of title/originaltitle when it is named sortable <br>
   * eg "Bourne Legacy, The" -> "The Bourne Legacy".
   * 
   * @param title
   *          the title
   * @return the original title
   */
  public static String removeCommonSortableName(String title) {
    if (title == null || title.isEmpty()) {
      return "";
    }
    for (String prfx : COMMON_TITLE_PREFIXES) {
      String delim = " "; // one spaces as delim
      if (prfx.matches(".*['`´]$")) { // ends with hand-picked delim, so no
                                      // space between prefix and title
        delim = "";
      }
      title = title.replaceAll("(?i)(.*), " + prfx, prfx + delim + "$1");
    }
    return title.trim();
  }

  /**
   * compares the given version (v1) against another one (v2)<br>
   * Special case:<br>
   * if we have SNAPSHOT, SVN or GIT version, and both are the same, return -1
   * 
   * @param v1
   *          given version
   * @param v2
   *          other version
   * @return < 0 if v1 is lower<br>
   *         > 0 if v1 is higher<br>
   *         = 0 if equal
   */
  public static int compareVersion(String v1, String v2) {
    if (v1.contains("-SNAPSHOT") && v1.equals(v2) || v1.equals("SVN") || v1.equals("GIT")) {
      // we have the same snapshot version - consider as potential lower (for nightly)
      // same for GIT - always "lower" to trigger update scripts!
      return -1;
    }
    String s1 = normalisedVersion(v1);
    String s2 = normalisedVersion(v2);
    return s1.compareTo(s2);
  }

  private static String normalisedVersion(String version) {
    return normalisedVersion(version, ".", 4);
  }

  private static String normalisedVersion(String version, String sep, int maxWidth) {
    // SNAPSHOT should be considered as lower version
    // so just removing does not work
    // add micromicro version to other
    if (!version.contains("-SNAPSHOT")) {
      version += ".0.0.1";
    }
    else {
      version = version.replace("-SNAPSHOT", "");
    }

    String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
    StringBuilder sb = new StringBuilder();
    for (String s : split) {
      sb.append(String.format("%" + maxWidth + 's', s));
    }
    return sb.toString();
  }

  public static String getLongestString(String[] array) {
    int maxLength = 0;
    String longestString = null;
    for (String s : array) {
      if (s.length() > maxLength) {
        maxLength = s.length();
        longestString = s;
      }
    }
    return longestString;
  }

  /**
   * check the given String not to be null - returning always a not null String
   * 
   * @param originalString
   *          the string to be checked
   * @return the originalString or an empty String
   */
  public static String getNonNullString(String originalString) {
    if (originalString == null) {
      return "";
    }
    return originalString;
  }
}
