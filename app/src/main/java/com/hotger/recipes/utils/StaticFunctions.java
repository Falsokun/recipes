package com.hotger.recipes.utils;


import java.util.ArrayList;

//TODO: ??? Stored in cloud

public class StaticFunctions {
//    public static ArrayList<Category> getCuisineCategories() {
//        ArrayList<Category> categoryList = new ArrayList<>();
//        String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Motherhood_and_apple_pie.jpg/1200px-Motherhood_and_apple_pie.jpg";
//        categoryList.add(new Category("American", "cuisine", url,"cuisine^cuisine-american"));
//        url = "http://www.savorthebook.com/sites/default/files/u4/16sproot2.JPG";
//        categoryList.add(new Category("Kid-Friendly", "cuisine", url,"cuisine^cuisine-kid-friendly"));
//        url = "https://auspost.com.au/content/dam/auspost_corp_microsites/travel-essentials/8-italian-food-expereinces.jpg.auspostimage.970*0.low.jpg";
//        categoryList.add(new Category("Italian", "cuisine", url,"cuisine^cuisine-italian"));
//        url = "https://img.grouponcdn.com/deal/wjfDJQXELMZrn8KE22D2sz/kitchenette_restaurant-900x540/v1/c700x420.jpg";
//        categoryList.add(new Category("Asian", "cuisine", url,"cuisine^cuisine-asian"));
//        url = "http://cancunmexicangrillon33rd.com/files/2014/04/002.jpg";
//        categoryList.add(new Category("Mexican", "cuisine", url,"cuisine^cuisine-mexican"));
////        categoryList.add(new Category("Southern & Soul Food", "southern & soul food kitchen", url,"cuisine^cuisine-southern"));
//        url = "https://fthmb.tqn.com/DOa_Cu-tXKsR2Vaw1fU44IObem4=/960x0/filters:no_upscale()/croissants-GettyImages-553199395-57b598813df78cd39c5f71c4.jpg";
//        categoryList.add(new Category("French", "cuisine", url,"cuisine^cuisine-french"));
//        url = "http://www.holliessteakhouse.com/wp-content/uploads/2014/10/3-1024x578.jpg";
//        categoryList.add(new Category("Southwestern", "cuisine", url,"cuisine^cuisine-southwestern"));
//        url = "https://images-na.ssl-images-amazon.com/images/I/81QwTNtASAL._SL1500_.jpg";
//        categoryList.add(new Category("Barbecue", "cuisine", url,"cuisine^cuisine-barbecue-bbq"));
//        url = "http://www.lotusindianrestaurant.ca/images/img-slide-3.jpg";
//        categoryList.add(new Category("Indian", "cuisine", url,"cuisine^cuisine-indian"));
//        url = "https://news.ludhianalive.com/wp-content/uploads/2017/07/Chinese-cuisine.jpg";
//        categoryList.add(new Category("Chinese", "cuisine", url,"cuisine^cuisine-chinese"));
//        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9B_8esDl8u-HuUqjwZu1l4itW9HZFfk_gpmpZrG7nPJjefVVXGA";
//        categoryList.add(new Category("Mediterranean", "cuisine", url,"cuisine^cuisine-mediterranean"));
//        url = "https://www.foodnewsfeed.com/sites/foodnewsfeed.com/files/styles/fnf_feature_front/public/top_photos/consumers-getting-more-taste-greek-cuisine.jpg";
//        categoryList.add(new Category("Greek", "cuisine", url,"cuisine^cuisine-greek"));
//        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTJNWH1aArGSeX07Pxo9TRKoymfUHKHgcR69ogfV3p1NlDTPh0O";
//        categoryList.add(new Category("English", "cuisine", url,"cuisine^cuisine-english"));
//        categoryList.add(new Category("Spanish", "cuisine", url,"cuisine^cuisine-spanish"));
//        categoryList.add(new Category("Thai", "cuisine", url,"cuisine^cuisine-thai"));
//        categoryList.add(new Category("German", "cuisine", url,"cuisine^cuisine-german"));
//        categoryList.add(new Category("Moroccan", "cuisine", url,"cuisine^cuisine-moroccan"));
//        categoryList.add(new Category("Irish", "cuisine", url,"cuisine^cuisine-irish"));
//        categoryList.add(new Category("Japanese", "cuisine", url,"cuisine^cuisine-japanese"));
//        categoryList.add(new Category("Cuban", "cuisine", url,"cuisine^cuisine-cuban"));
//        categoryList.add(new Category("Hawaiian", "cuisine", url,"cuisine^cuisine-hawaiian"));
//        categoryList.add(new Category("Swedish", "cuisine", url,"cuisine^cuisine-swedish"));
//        categoryList.add(new Category("Hungarian", "cuisine", url,"cuisine^cuisine-hungarian"));
//        categoryList.add(new Category("Portuguese", "cuisine", url,"cuisine^cuisine-portuguese"));
//        return categoryList;
//    }
//
//    public static ArrayList<Category> getHolidayCategories() {
//        ArrayList<Category> categoryList = new ArrayList<>();
//        String url = "https://i2-prod.mirror.co.uk/incoming/article9203172.ece/ALTERNATES/s615b/Christmas-dinner.jpg";
//        categoryList.add(new Category("Christmas", "holiday", url,"holiday^holiday-christmas"));
//        url = "https://media.timeout.com/images/102943931/630/472/image.jpg";
//        categoryList.add(new Category("Thanksgiving", "holiday", url,"holiday^holiday-thanksgiving"));
//        url = "https://www.ecellulitis.com/wp-content/uploads/2014/07/summer_detox_juices_m.jpg";
//        categoryList.add(new Category("Summer", "holiday", url,"holiday^holiday-summer"));
//        url = "https://thumbs.dreamstime.com/b/fall-food-still-life-hot-tea-produce-44717155.jpg";
//        categoryList.add(new Category("Fall", "holiday", url,"holiday^holiday-fall"));
//        url = "http://www.delsfoods.com/wp-content/uploads/2013/12/new-year-food-ideas.jpg";
//        categoryList.add(new Category("New year", "holiday", url,"holiday^holiday-new-year"));
//        url = "http://monishgujral.com/new/wp-content/uploads/2013/03/Winter-comfort-foods.jpg";
//        categoryList.add(new Category("Winter", "holiday", url,"holiday^holiday-winter"));
//        url = "http://www.theunprocessedpantry.com/images/seasonalfood.jpg";
//        categoryList.add(new Category("Spring", "holiday", url,"holiday^holiday-spring"));
//        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJsZL6FpC2ZiZv8vzcmHgFmI456D0bX-ro5OhLgF9GplmE4a2u";
//        categoryList.add(new Category("Halloween", "holiday", url,"holiday^holiday-halloween"));
//        url = "https://s-media-cache-ak0.pinimg.com/originals/79/f1/61/79f161fe46950e18ee9ce83f6f2e0b11.jpg";
//        categoryList.add(new Category("Valentine's Day", "holiday", url,"holiday^holiday-valentines-day"));
//        url = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2012/10/11/0/FN_Potato-Latkes_s4x3.jpg.rend.hgtvcom.966.725.suffix/1382542094439.jpeg";
//        categoryList.add(new Category("Hanukkah", "holiday", url,"holiday^holiday-hanukkah"));
//        url = "https://fthmb.tqn.com/6XnxDcJQN4xVfVt1bC8UN1qIVIc=/768x0/filters:no_upscale()/FeastofPassoverGettyImages-91641494-5665ce243df78ce161c4fa90.jpg";
//        categoryList.add(new Category("Passover", "holiday", url,"holiday^holiday-passover"));
//        url = "http://goodtoknow.media.ipcdigital.co.uk/111/00001471f/0340_orh100000w614/Easter-eggs.jpg";
//        categoryList.add(new Category("Easter", "holiday", url,"holiday^holiday-easter"));
//        url = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2014/1/29/1/FN_St-Patricks-Day-Green-Velvet-Cupcake-Shamrocks_s4x3.jpg.rend.hgtvcom.966.725.suffix/1391199935895.jpeg";
//        categoryList.add(new Category("St. Patrick's Day", "holiday", url,"holiday^holiday-st-patricks-day"));
//        return categoryList;
//    }
//
//    public static ArrayList<Category> getCourse() {
//        ArrayList<Category> categoryList = new ArrayList<>();
//        String url = "https://www.vegansociety.com/sites/default/files/Aubergine%20and%20Chickpea%20Penne.jpg";
//        categoryList.add(new Category("Main Dishes", "course", url,"course^course-Main Dishes"));
//        url = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2015/8/28/0/FN_Recipe-Group-Shot_s4x3.jpg.rend.hgtvcom.966.725.suffix/1440786596287.jpeg";
//        categoryList.add(new Category("Desserts", "course", url,"course^course-Desserts"));
//        url = "http://cdn-image.myrecipes.com/sites/default/files/styles/4_3_horizontal_-_1200x900/public/roasted-baby-spring-vegetables-ck.jpg?itok=hhsnnB2T";
//        categoryList.add(new Category("Side Dishes", "course", url,"course^course-Side Dishes"));
//        url = "http://www.theflavorbender.com/wp-content/uploads/2016/05/Grilled-Shrimp-and-Mango-Appetizers-5128.jpg";
//        categoryList.add(new Category("Appetizers", "course", url,"course^course-Appetizers"));
//        url = "https://www.budgetbytes.com/wp-content/uploads/2017/03/Super-Fresh-Salad-V3.jpg";
//        categoryList.add(new Category("Salads", "course", url,"course^course-Salads"));
//        url = "https://inm-baobab-prod-eu-west-1.s3.amazonaws.com/public/inm/media/image/2017/02/16/58572154Brunch2.jpg";
//        categoryList.add(new Category("Breakfast and Brunch", "course", url,"course^course-Breakfast and Brunch"));
//        url = "https://cdn2.tmbi.com/TOH/Images/Photos/37/1200x1200/Christmas-Star-Twisted-Bread_EXPS_THN16_190439_C06_21_2b.jpg";
//        categoryList.add(new Category("Breads", "course", url,"course^course-Breads"));
//        url = "https://i.ytimg.com/vi/L1TFnkm1TG8/maxresdefault.jpg";
//        categoryList.add(new Category("Soups", "course", url,"course^course-Soups"));
//        url = "https://www.thedailymeal.com/sites/default/files/styles/hero_image_breakpoints_theme_tdmr_lg_1x/public/2016/06/09/0-fruit%20infused%20ALSO%20MAIN_1.jpg?itok=5Bh_6KmE&timestamp=1519146922";
//        categoryList.add(new Category("Beverages", "course", url,"course^course-Beverages"));
//        url = "https://www.consumer.org.hk/sites/consumer/files/w_choice_article/471/W1601CHOI_condiments-sauces01ps.jpg";
//        categoryList.add(new Category("Condiments and Sauces", "course", url,"course^course-Condiments and Sauces"));
//        url = "https://punchdrink.com/wp-content/uploads/2017/04/Slide-Clover-Club-Egg-White-Cocktails-Boston-Sour-Clover-Club-Recipe.jpg";
//        categoryList.add(new Category("Cocktails", "course", url,"course^course-Cocktails"));
//        url = "https://d1bjorv296jxfn.cloudfront.net/s3fs-public/styles/recipes_header_image/public/1753_Healthly-Snack-Platter.jpg?itok=kpJRkSAk";
//        categoryList.add(new Category("Snacks", "course", url,"course^course-Snacks"));
//        return categoryList;
//    }
//
//    public static ArrayList<Category> getDiets() {
//        //Lacto vegetarian, Ovo vegetarian, Pescetarian, Vegan, Vegetarian
//        ArrayList<Category> categoryList = new ArrayList<>();
//        String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Soy-whey-protein-diet.jpg/1200px-Soy-whey-protein-diet.jpg";
//        categoryList.add(new Category("Lacto vegetarian", "diet", url,"388^Lacto vegetarian"));
//        url = "https://img.aws.livestrongcdn.com/ls-article-image-673/ds-photo/getty/article/94/215/480175332.jpg";
//        categoryList.add(new Category("Ovo vegetarian", "diet", url,"389^Ovo vegetarian"));
//        url = "https://gymjunkies.com/wp-content/uploads/2017/05/shutterstock_470363480-1.jpg";
//        categoryList.add(new Category("Pescetarian", "diet", url,"390^Pescetarian"));
//        url = "https://fthmb.tqn.com/Q0zAwbbT_HjA4BzK4lgJDhbXZWM=/2000x1333/filters:fill(auto,1)/vegan_vegetables-484152000-588893413df78c2ccd8d08b2.jpg";
//        categoryList.add(new Category("Vegan", "diet", url,"386^Vegan"));
//        url = "https://momentumlab.sg/wp-content/uploads/2016/01/momentum-lab_healthy-breakfast-1.jpg";
//        categoryList.add(new Category("Lacto-ovo vegetarian", "diet", url,"387^Lacto-ovo vegetarian"));
//        url = "https://images.agoramedia.com/everydayhealth/gcms/Can-a-Paleo-Diet-Treat-UC-1440x810.jpg?width=722";
//        categoryList.add(new Category("Paleo", "diet", url,"403^Paleo"));
//        return categoryList;
//    }

    public ArrayList<String> getIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("honey");
        ingredients.add("sugar");
        return ingredients;
    }
}
