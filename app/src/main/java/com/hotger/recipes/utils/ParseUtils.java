package com.hotger.recipes.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;

import java.net.URL;
import java.net.HttpURLConnection;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.view.redactor.RedactorActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    private static Pattern instructionsPattern = Pattern.compile("(instructions?|directions?|preparations?" +
            "|приготовление|инструкции|(инструкция|способ|рецепт|порядок) приготовления|пошаговый( фото)? рецепт" +
            "( приготовления)|подготовка)");//.*?( |:|\n|<.*?>)
    //    private static Pattern ingredientsPattern = Pattern.compile("(ингредиенты|вам понадоб[ия]тся|ingredients)");//.*?( |:|\n|<.*?>)
    private static Pattern portionsPattern = Pattern.compile("(на [\\d-]+ (персон.?|порци.?)|количество " +
            "(порций|персон)? [\\d-]+|([\\d-]+)? ?порци. (\\d+)?)");
    private static Pattern timePattern = Pattern.compile("(общее время )?([\\d-]+ час.*? )?[\\d-]+ мин(\\w+|\\.| )");
    private static Pattern caloriesPattern = Pattern.compile("(калорий.*? )?\\d+( ккал| калорий)");
    //    private static Pattern relatedPattern = Pattern.compile("(похож.*? рецепт?| вам (также|могут) понравит|подбор( рецепта)?)");
    private static String[] keyWordsEn = {"cook for", "bon appetit", "rest", "serving", "serve", "hour", "until", "bake",
            "put", "hour", "hr", "plate", "heat", "oven", "degree", "mix", "dry", "dust", "scatter", "place",
    "cool", "baking", "pull", "cut", "skillet", "drain", "chop", "slice", "sprinkle", "fry", "fold",
            "combine", "stir", "toss", "stuff", "prepare", "cut", "fridge", "spoon", "warm", "add",
            "whisk", "melt", "fill", "frige", "cover", };
    private static String[] keyWordsRu = {"приятного аппетита", "подавать к столу", "посол", "запек",
            "постоять", "остудит", "час", "постав", "тарелк", "лепит",
            "подавать", "подавай", "украс", "спасибо", "посол", "сахар", "обжар", "очист",
            "перемеш", "взбить", "подготов", "смешат", "духовк", "смешай", "накрой", "пропита", "грей",
            "грел", "противень", "градус", "отделит", "взбей", "однородн", "охлажд", "охлад", "скалк",
            "кастрюл", "тщательно", "резат", "режьт", "холодильник", "затем",
            "молок", "стакан", "мелко", "выпека", "растер", "залит", "замесит", "градус"};
    private static String[] keywords;

    public static void parseRecipe(String url, Context context, boolean isFromApi) {
        Thread t = new Thread(() -> {
            initData();
            Recipe recipe = new Recipe();
            String recipeText;
            String finalUrl = getRedirectIfAny(url);
            String body = getBody(getRecipeText(finalUrl));
            recipeText = getRecipeSteps(Jsoup.parse(body), isFromApi);
            recipe.setPreparations(fromHtml(recipeText));
            if (!isFromApi) {
                getRecipeInfo(recipeText, recipe);
                Intent intent = new Intent(context, RedactorActivity.class);
                intent.putExtra(Utils.IntentVars.RECIPE_OBJ, recipe);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(YummlyAPI.REC_DIRECTIONS);
                intent.putExtra(YummlyAPI.REC_DIRECTIONS, recipeText);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        t.start();
    }

    private static String fromHtml(String recipeText) {
        return Html.fromHtml(recipeText).toString().replaceAll("\n+", "\n");
    }

    private static void initData() {
        keywords = new String[keyWordsEn.length + keyWordsRu.length];
        System.arraycopy(keyWordsEn, 0, keywords, 0, keyWordsEn.length);
        System.arraycopy(keyWordsRu, 0, keywords, keyWordsEn.length, keyWordsRu.length);
    }

    //тут надо найти продукты, время, количество калорий, количество порций, название
    private static void getRecipeInfo(String recipeText, Recipe recipe) {
        String str = recipeText.replaceAll("</?.*?>", " ");
        str = str.replaceAll("[:\\-\"]", "");
        str = str.replaceAll("&nbsp;", " ");
        str = str.replaceAll("[\t .]+]", " ");
        int portions = getNumberByPattern(str, portionsPattern);
        int time = getNumberByPattern(str, timePattern);
        int calories = getNumberByPattern(str, caloriesPattern);
        recipe.setCookTimeInMinutes(time);
        recipe.setCalories(calories);
        recipe.setNumberOfServings(portions);
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

    private static String getRecipeSteps(Element htmlText, boolean isFromApi) {
        Element el = removeComments(htmlText);
        Document newDoc = removeRedundantLabels(el.toString());
        return sortByDivs(newDoc.body()).toString();
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

    private static Element getElementWithInstructions(Element div) {
        if (div.select("div,section,ul,table").size() == 0)
            return div;

        Matcher matcher;
        Element out = div;
        for (Element elem : div.select("div,section,ul,table")) {
            matcher = instructionsPattern.matcher(elem.toString().toLowerCase());
            if (matcher.find()) {
                if (out.toString().length() > elem.toString().length()
                        && elem.toString().toLowerCase().length() > matcher.group().length() * 3) {
                    out = elem;
                }
            }
        }

        return out;
    }

    private static Element sortByDivs(Element doc) {
        Elements elements = doc.select(":root > div");
        Element firstChild = null;
        int divCount = 0;
        for (Element element : elements) {
            int tempCount = 0;
            for (String key : keywords) {
                tempCount += countSubString(element.toString().toLowerCase(), key);
            }

            if (tempCount != 0 && tempCount >= divCount) {
                firstChild = element;
                divCount = tempCount;
            }
        }
//        Element element = sortByNotDivs(firstChild);
        return getBestElement(getDirectElements(firstChild), true);
    }

    private static Element removeChildrenNesting(Element firstChild) {
        if (firstChild == null || firstChild.children() == null || firstChild.children().size() == 0)
            return firstChild;

        if (firstChild.children().size() > 1) {
            return firstChild;
        }

        return removeChildrenNesting(firstChild.child(0));
    }

//    private static Element sortWithManyChildren(Element firstChild) {
//        Elements nestedDivs = firstChild.select(":root > div");
//        if (nestedDivs.size() == 1) {
//            Element div = firstChild.select(":root > div").first();
//            if (countKeyWords(div) == countKeyWords(firstChild)) {
//                return removeChildrenNesting(div);
//            }
//        }
//
//        if (countDifferentChildTags(firstChild) == nestedDivs.size()) {
//            return getBestElement(nestedDivs, true);
//        } else {
//            return getBestElement(getDirectElements(firstChild), false);
//        }
//    }

    private static Elements getDirectElements(Element el) {
        Elements elements = new Elements();
        elements.addAll(el.select(":root > div"));
        elements.addAll(el.select(":root > section"));
        elements.addAll(el.select(":root > ul"));
        elements.addAll(el.select(":root > table"));
        elements.addAll(el.select(":root > ol"));
        return elements;
    }
    private static int countDifferentChildTags(Element el) {
        if (el == null || el.children().size() == 0)
            return 0;

        return el.select(":root > div").size()
                + el.select(":root > section").size()
                + el.select(":root > ul").size()
                + el.select(":root > table").size()
                + el.select(":root > ol").size();
    }

    private static Element getBestElement(Elements doc, boolean isContinue) {
        Element res = null;
        int divCount = 0;
        for (Element element : doc) {
            Element temp = removeChildrenNesting(element);
            int tempCount = countKeyWords(temp);
            if (tempCount != 0 && tempCount >= divCount) {
                res = temp;
                divCount = tempCount;
            }
        }

        if (isContinue) {
            if (countDifferentChildTags(res) == res.select(":root > div").size()) {
                if (res.select("div").size() - 1 > res.select(":root > div").size()) {
                    return getBestElement(res.select(":root > div"), true);
                } else {
                    return res;
                }
            } else {
                return getBestElement(getDirectElements(res), false);
            }
        }

        return res;
    }

    private static int countKeyWords(Element element) {
        int tempCount = 0;
        for (String key : keywords) {
            tempCount += countSubString(element.toString().toLowerCase(), key);
        }

        return tempCount;
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

    private static String getRecipeText(String path) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL( path );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
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

    private static String getBody(String plainText) {
        Pattern mPattern = Pattern.compile("<body.*?/body>");
        Matcher m = mPattern.matcher(plainText);
        if (m.find()) {
            String bodyText = m.group();
            bodyText = bodyText.replaceAll("<script.*?/script>", "");
            return bodyText;
        }

        return "";
    }

    private static Document removeRedundantLabels(String htmlBody) {
//            //.*? - any character until first match
        String str = removeIrrelevant(htmlBody);
//        str = str.replaceAll(" (class|id|itemprop|align|for|style|itemscope itemtype)=[\"'].*?[\"']", "");
        str = str.replaceAll(" [^ ]*?=[\"'].*?[\"']>", ">");
        str = str.replaceAll("\\t", "");
        str = str.replaceAll("</?(i|b|span|strong|br|h\\d|textarea)>", "");
        str = str.replaceAll("[ ]{2,}", " ");
        str = str.replaceAll("<(tbody|td|tr).*?>", "");
        str = str.replaceAll("</(tbody|td|tr)>", "");
        str = str.replaceAll("<(section|table|article)", "<div");
        str = str.replaceAll("/(section|table|article)>", "/div>");
        str = removeEmptyTags(str);
        return Jsoup.parse(str);
    }

    private static String removeEmptyTags(String str) {
        String tagPString = "<(?<tag>\\w+)>((?: |\\n|\\t|&nbsp;)*?|.{1,10})</\\k<tag>>";
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
        String[] irrelevantTags = {"option", "nav", "form", "aside", "input", "img", "style", "iframe", "header"};

        String out = str
//                .replaceAll("[\"']http.*?[\"']", "")
                .replaceAll("<!--.*?-->", "")
                .replaceAll("<img.*?>", "")
                .replaceAll("</img>", "")
                .replaceAll("href=\".*?\"", "")
                .replaceAll("<a.*?/a>", "")
                .replaceAll("<input.*?>", "");
        for (String tag : irrelevantTags) {
            out = out.replaceAll("<" + tag + ".*?>.*?</" + tag + "?>", "");
        }

        return out;
    }

    //надо пересмотреть, может эта хрень вообще делается гораздо проще
    private static Element removeComments(Element doc) {
        Elements elements = new Elements();
        for(Element element : doc.getAllElements()) {
            if (element.classNames().contains("comment") || element.id().contains("comment")) {
                elements.add(element);
                element.remove();
            }
        }

        return doc;
    }
}
