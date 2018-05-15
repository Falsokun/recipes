package com.hotger.recipes.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    private static Pattern instructionsPattern = Pattern.compile("(instructions?|directions?|preparations?" +
            "|приготовление|инструкции|(инструкция|способ|рецепт|порядок) приготовления|пошаговый( фото)? рецепт( приготовления)|подготовка)");//.*?( |:|\n|<.*?>)
    private static Pattern ingredientsPattern = Pattern.compile("(ингредиенты|вам понадоб[ия]тся|ingredients)");//.*?( |:|\n|<.*?>)
    private static Pattern portionsPattern = Pattern.compile("(на \\d+ (персон.?|порци.?)|количество (порций|персон)? \\d+|(\\d+)? ?порци. (\\d+)?)");
    private static Pattern timePattern = Pattern.compile("(общее время )?(\\d+ час.*? )?\\d+ мин(\\w+|\\.| )");
    private static Pattern caloriesPattern = Pattern.compile("(калорий.*? )?\\d+( ккал| калорий)");
//    Количество порций
//    Порций
//    порции
//    количество персон
    private static Pattern relatedPattern = Pattern.compile("(похож.*? рецепт?| вам (также|могут) понравит|подбор( рецепта)?)");
    private static String[] keyWordsEn = {"cook for", "bon appetit", "rest", "serving", "serve", "hour", "until", "bake"};
    private static String[] keyWordsRu = {"приятного аппетита", "подавать к столу", "посол", "запек",
            "постоять", "остудит", "час", "постав", "тарелк", "лепит", "сковород",
            "подавать", "подавай", "украс", "спасибо", "посол", "сахар", "обжар", "сковород",
            "перемеш", "взбить", "подготов", "смешат", "духовк", "смешай", "накрой", "пропита", "грей",
            "грел", "противень", "градус", "отделит", "взбей", "однородн", "охлажд", "охлад", "скалк",
            "кастрюл", "тщательно", "резат", "режьт", "холодильник", "ложк", "затем",
            "молок", "стакан", "мелко", "выпека", "растер", "залит", "замесит"};

    public static void parseRecipe(String url, Context context, boolean isFromApi) {
        Thread t = new Thread(() -> {
            String recipeText = "";
//            String finalUrl = getRedirectIfAny(url);
//            String body = getBody(getRecipeText(finalUrl));
//            recipeText = removeRedundantLabels(body);
//            recipeText = getRecipeSteps(recipeText, context, isFromApi);
            if (!isFromApi) {
//                displayAlert(recipeText, context);
//                getRecipeInfo(recipeText, context);
            } else {
                Intent intent = new Intent(YummlyAPI.REC_DIRECTIONS);
                intent.putExtra(YummlyAPI.REC_DIRECTIONS, recipeText);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        t.start();
    }

    //тут надо найти продукты, время, количество калорий, количество порций, название
    private static void getRecipeInfo(String recipeText, Context context) {
        String str = recipeText.replaceAll("</?.*?>", "");
        str = str.replaceAll("[:\\-\"]", "");
        str = str.replaceAll("&nbsp;", " ");
        int portions = getNumberByPattern(str, portionsPattern);
        int time = getNumberByPattern(str, timePattern);
        int calories = getNumberByPattern(str, caloriesPattern);
    }

    private static int getNumberByPattern(String str, Pattern pattern) {
        String portions = "";
        Matcher m = pattern.matcher(str.toLowerCase());
        if (m.find()) {
            portions = m.group();
        }

        Pattern number = Pattern.compile("\\d+");
        m = number.matcher(portions);
        if (m.find()) {
            return Integer.valueOf(m.group());
        }

        return 0;
    }

    public static String getRecipeSteps(String recipeText, Context context, boolean isFromApi) {
        //минуты в большом количестве встречаются в других местах
        String[] keywords = new String[keyWordsEn.length + keyWordsRu.length];
        System.arraycopy(keyWordsEn, 0, keywords, 0, keyWordsEn.length);
        System.arraycopy(keyWordsRu, 0, keywords, keyWordsEn.length, keyWordsRu.length);

        Element div = getElementWhichContainsMost(recipeText,
                keywords);
        div = getElementWithInstructions(div, recipeText);
//            div = getFinalRecipeDiv(div);
        String result = getFromInstructions(div.toString());
//            result = result.replaceAll(">(\n| )*<", "><");
        result = result.replaceAll("([\n]+[ ]{2,})+", "\n");
        return result;
    }

    private static void displayAlert(String result, Context context) {
    }

    private static String getFromInstructions(String str) {
        Matcher matcher = instructionsPattern.matcher(str.toLowerCase());
        int index = 0;
        while (matcher.find()) {
            String match = matcher.group();
            index = str.substring(index).toLowerCase().indexOf(match) + index + match.length();
        }

        return str.substring(index)
                .replaceAll("<.*?/?>", "");
    }

    private static Element getElementWithInstructions(Element div, String text) {
        if (div.select("div,section,ul,table").size() == 0)
            return div;

        Matcher matcher = ingredientsPattern.matcher(text);
        int ingredientIndex = 0;
        if (matcher.find()) {
            ingredientIndex = text.indexOf(matcher.group());
        }

        Element out = div;
        for (Element elem : div.select("div,section,ul,table")) {
            matcher = instructionsPattern.matcher(elem.toString().toLowerCase());
            if (matcher.find()) {
                if (out.toString().length() > elem.toString().length()
                        && elem.toString().toLowerCase().length() > matcher.group().length() * 3) {
//                    if (ingredientIndex < text.indexOf(elem.toString())) {
                    out = elem;
//                    }
                }
            }
        }

        return out;
    }

    //ТУТ ВСЕ ПЛОХО
    private static Element getElementWhichContainsMost(String str, String[] keyWords) {
        Element closest = new Element("tag");
        Document doc = Jsoup.parse(str);
        Elements elements = doc.body().select("div,section,table,ul");
        int maxTemp = 0;
        int closestCount = 0;
        Element maxElem = new Element("tag");
        for (Element element : elements) {
            int tempCount = 0;
            for (String key : keyWords) {
                tempCount += countSubString(element.toString().toLowerCase(), key);
            }

            if (tempCount < 5)
                continue;

            //сужение
            if (tempCount >= closestCount) {
                closestCount = tempCount;
                closest = element;
            } else { // другой блок
                if (tempCount >= maxTemp) {
                    maxElem = element;
                    maxTemp = tempCount;
                }
            }
        }

        //тут вообще должно быть другое условие, надо подумать какое
        //может соотношение длины и количества попавших
        if (maxTemp / (double) maxElem.toString().length() >
                closestCount / (double) closest.toString().length()) {
            return maxElem;
        } else {
            return closest;
        }
    }

    private static boolean isGoodKoeff(int clCount, Element closest, int elCount, Element element) {
        double elWords = element
                .toString()
                .replaceAll("<.*?>", "")
                .split("( |\n|&nbsp;)").length;
        double clWords = closest
                .toString()
                .replaceAll("<.*?>", "")
                .split("( |\n|&nbsp;)").length;
        return elCount == 0
                || (elCount / elWords) / (clCount / clWords) > 0.8;
    }

    private static int countSubString(String str, String substr) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = str.indexOf(substr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += substr.length();
            }
        }

        return count;
    }

    private static String getRedirectIfAny(String stringUrl) {
        HttpURLConnection ucon = null;
        try {
            URL url = new URL(stringUrl);
            ucon = (HttpURLConnection) url.openConnection();
            ucon.setInstanceFollowRedirects(false);
            if (ucon.getHeaderField("Location") == null) {
                return stringUrl;
            }

            URL secondURL = new URL(ucon.getHeaderField("Location"));
            ucon.disconnect();
            return secondURL.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ucon != null) {
                ucon.disconnect();
            }
        }

        return stringUrl;
    }

    public static String getRecipeText(String path) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(path);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                stringBuffer.append(inputLine);
            }

            if (stringBuffer.toString().contains("charset=windows-1251")) {
                in = new BufferedReader(new InputStreamReader(url.openStream(),
                        "Windows-1251"));
                stringBuffer = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    stringBuffer.append(inputLine);
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public static String getBody(String plainText) {
        Pattern mPattern = Pattern.compile("<body.*?/body>");
        Matcher m = mPattern.matcher(plainText);
        if (m.find()) {
            String bodyText = m.group();
            bodyText = bodyText.replaceAll("<script.*?/script>", "");
            return bodyText;
        }

        return "";
    }

    public static String removeRedundantLabels(String htmlBody) {
//            //.*? - any character until first match
        String str = removeIrrelevant(htmlBody);
        str = removeComments(str);
//        str = str.replaceAll(" (class|id|itemprop|align|for|style|itemscope itemtype)=[\"'].*?[\"']", "");
        str = str.replaceAll(" [^ ]*?=[\"'].*?[\"']>", ">");
        str = str.replaceAll("\\t", "");
        str = str.replaceAll("</?(i|b|span|h\\d||textarea)>", "");
        str = str.replaceAll("[ ]{2,}", " ");
        str = removeEmptyTags(str);
//        String[] textTags = {"span", "p", };
//        str = str.replaceAll()

        //remove all tags
//        bodyText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ")
        return str;
    }

    private static String removeEmptyTags(String str) {
        String tagPString = "<(?<tag>\\w+)?>((?: |\\t|&nbsp;)*?|.{1,6})</\\k<tag>>";
        Pattern tagPattern = Pattern.compile(tagPString);
        Matcher m = tagPattern.matcher(str);
        while (m.find()) {
            str = str.replaceAll(tagPString, "");
            m = tagPattern.matcher(str);
        }
        return str;
    }

    private static String removeIrrelevant(String str) {
        //img и другие закрывающие <img/> теги надо удалять по-другому
        String[] irrelevantTags = {"option", "nav", "form", "input", "img", "style", "iframe", "header"};

        String out = str
//                .replaceAll("[\"']http.*?[\"']", "")
                .replaceAll("<!--.*?-->", "")
                .replaceAll("<img.*?>", "")
                .replaceAll("</img>", "")
                .replaceAll("href=\".*?\"", "");
        for (String tag : irrelevantTags) {
            out = out.replaceAll("<" + tag + ".*?>.*?</" + tag + "?>", "");
        }

        return out;
    }

    //ЧОТ ОЧЕНЬ ДОЛГО
    private static String removeComments(String str) {
        Pattern mPattern = Pattern.compile("comment.*?>");
        Pattern openTag = Pattern.compile("<(.*?) ");
        Pattern tagWithClassesPattern;
        Matcher tagMatcher;
        Matcher m = mPattern.matcher(str);
        int lastIndex = 0;
        ArrayList<String> comments = new ArrayList<>();
        while (m.find()) {
            String regString = m.group();
            int tempLast = lastIndex;
            lastIndex = str.indexOf(regString, lastIndex) + regString.length();
            //substring containing comment
            String substr = str.substring(tempLast, lastIndex);
            String subAfter = str.substring(lastIndex);

            //regString should be screened
            tagWithClassesPattern = Pattern.compile("<[0-9a-zA-Z:=\" -]*?" + Pattern.quote(regString));
            tagMatcher = tagWithClassesPattern.matcher(substr);
            if (tagMatcher.find()) {
                String tag = tagMatcher.group();
                tagMatcher = openTag.matcher(tag);
                tagMatcher.find();
                tag = tagMatcher.group(1);

                // вот тут должно быть не indexOf а функция, которая пропускает </tag, если до этого были <tag
                int closeIndex = getEnclosingIndex(subAfter, tag);
                if (closeIndex == -1) {
                    continue;
                }

                int openBrace = substr.lastIndexOf("<" + tag);
                String comment = str.substring(tempLast + openBrace, tempLast +
                        substr.length() + closeIndex + 3 + tag.length());
                comments.add(comment);
            }
        }

        for (String comment : comments) {
            if (str.contains(comment))
                str = str.replaceFirst(Pattern.quote(comment), "");
        }

        return str;
    }

    private static int getEnclosingIndex(String str, String tag) {
        String copyStr = str;
        int lastIndex = str.indexOf("</" + tag + ">");
        if (lastIndex == -1)
            return -1;
        String substring = copyStr.substring(0, lastIndex);
        while (lastIndex != -1) {
            if (substring.contains("<" + tag)) {
                copyStr = copyStr.replaceFirst("<" + tag, "<anytag");
                copyStr = copyStr.replaceFirst("</tag", "</anytag");
                lastIndex = str.indexOf("</" + tag);
                substring = copyStr.substring(0, lastIndex);
            } else {
                return lastIndex;
            }
        }

        return -1;
    }
}
