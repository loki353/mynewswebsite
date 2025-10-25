import com.google.gson.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsWebApp {
    private static final String API_KEY = "9dd57f7234b64b9288524c29a065fe57"; // your API key
    private static final int PORT = 8080;
    private static final AtomicInteger visitorCount = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new MainHandler());

        server.createContext("/images", exchange -> {
            String path = "images" + exchange.getRequestURI().getPath().replace("/images", "");
            File file = new File(path);
            if (file.exists()) {
                String contentType = "image/png";
                if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = fis.read(buffer)) >= 0) os.write(buffer, 0, count);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });

        server.setExecutor(null);
        System.out.println("✅ Server running at http://localhost:" + PORT);
        server.start();
    }

    static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int visitors = visitorCount.incrementAndGet();
            String query = exchange.getRequestURI().getQuery();
            String category = null;
            if (query != null && query.contains("category=")) {
                category = query.split("category=")[1];
            }

            String response;
            if (category == null || category.isEmpty()) {
                response = getMainPage(visitors);
            } else {
                response = getCategoryPage(category);
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static String getMainPage(int visitors) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Articylst - News Aggregator</title>");
        html.append("<style>");
        html.append(":root { --bg:#0b0c10; --text:#e1e1e1; --card:#1f2833; --primary:#66fcf1; }");
        html.append("[data-theme='light'] { --bg:#ffffff; --text:#0b0c10; --card:#f4f4f4; --primary:#0077cc; }");
        html.append("body{margin:0;padding:0;font-family:'Segoe UI',sans-serif;background:var(--bg);color:var(--text);} ");
        html.append(".container{width:90%;margin:auto;padding:40px 0;text-align:center;} ");
        html.append(".hero{padding:60px 20px;} .hero h1{font-size:3em;color:var(--primary);margin-bottom:10px;} ");
        html.append(".hero p{font-size:1.2em;max-width:800px;margin:auto;line-height:1.6em;} ");
        html.append(".categories{display:flex;flex-wrap:wrap;justify-content:center;gap:20px;margin-top:30px;} ");
        html.append(".category-card{background:var(--card);width:180px;height:180px;border-radius:12px;overflow:hidden;text-decoration:none;color:var(--text);display:flex;flex-direction:column;align-items:center;justify-content:center;transition:transform 0.3s;} ");
        html.append(".category-card img{width:100%;height:100px;object-fit:contain;} ");
        html.append(".category-card span{margin-top:8px;font-weight:bold;} ");
        html.append(".category-card:hover{transform:translateY(-5px);box-shadow:0 10px 25px rgba(0,0,0,0.5);} ");
        html.append(".team{display:flex;flex-wrap:wrap;justify-content:center;gap:20px;margin-top:50px;} ");
        html.append(".team-card{background:var(--card);padding:20px;border-radius:12px;width:250px;box-shadow:0 6px 15px rgba(0,0,0,0.5);transition:transform 0.3s;text-align:center;} ");
        html.append(".team-card:hover{transform:translateY(-5px);} ");
        html.append(".team-card img{width:150px;height:150px;object-fit:cover;border-radius:50%;margin-bottom:15px;border:3px solid var(--primary);} ");
        html.append(".guide-card{background:var(--primary);color:#fff;padding:25px;border-radius:12px;width:300px;margin:20px auto;box-shadow:0 6px 15px rgba(0,0,0,0.5);} ");
        html.append(".toggle-btn{position:fixed;top:10px;right:10px;padding:10px 20px;cursor:pointer;border:none;border-radius:8px;background:var(--primary);color:#fff;font-weight:bold;} ");
        html.append("@media(max-width:768px){.team-card,.category-card,.guide-card{width:90%;}}");
        html.append("</style>");
        html.append("<script>function toggleTheme(){let theme=document.documentElement.getAttribute('data-theme');document.documentElement.setAttribute('data-theme',theme==='light'?'dark':'light');}</script>");
        html.append("</head><body data-theme='light'>");
        
        html.append("<div class='container'>");

        // Hero section with About Website paragraph
        html.append("<div class='hero'>");
html.append("<h1>Articlyst News Aggregator</h1>");
html.append("<p>🗞️ Welcome to Articlyst – Your Daily Dose of Real News!<br><br>"
    + "Tired of scrolling endlessly to find what’s happening around you?<br>"
    + "Articlyst brings everything you need — in one smart, simple, and beautiful place.<br><br>"
    + "We collect the latest and most trusted news from reliable sources and deliver it to you instantly. "
    + "From breaking national headlines to global events, technology trends, business updates, sports highlights, "
    + "health breakthroughs, and entertainment buzz — you’ll find it all here!<br><br>"
    + "Our goal is to make news reading easy, fast, and enjoyable.<br>"
    + "Every story on Articlyst is carefully selected and neatly presented so that you can stay updated without wasting time. "
    + "Whether you’re a student, professional, or just someone who loves staying informed, Articlyst is made for you.<br><br>"
    + "Discover trending topics, inspiring stories, and must-know updates that shape our world every day. "
    + "With Articlyst, you don’t just read the news — you experience it.<br><br>"
    + "📱 Simple to use, 💬 easy to understand, and ⚡ powered by real-time updates — "
    + "Articlyst keeps you closer to the world like never before.<br><br>"
    + "Explore. Learn. Stay Ahead — with Articlyst: Where Every Headline Matters</p>");
html.append("</div>");


        html.append("<h2>Explore Categories</h2>");
        html.append("<div class='categories'>");
        html.append("<a class='category-card' href='/?category=india'><img src='/images/india.png'><span>India</span></a>");
        html.append("<a class='category-card' href='/?category=world'><img src='/images/world.png'><span>World</span></a>");
        html.append("<a class='category-card' href='/?category=movies'><img src='/images/movies.png'><span>Movies</span></a>");
        html.append("<a class='category-card' href='/?category=sport'><img src='/images/sports.png'><span>Sport</span></a>");
        html.append("<a class='category-card' href='/?category=health'><img src='/images/health.png'><span>Health</span></a>");
        html.append("<a class='category-card' href='/?category=science'><img src='/images/science.png'><span>Science</span></a>");
        html.append("<a class='category-card' href='/?category=business'><img src='/images/business.png'><span>Business</span></a>");
        html.append("</div>");

        html.append("<h2>Our Team</h2>");
        html.append("<div class='team'>");
        html.append("<div class='team-card'><img src='/images/loki.jpg'><h3>B Lokesh</h3><p>A passionate Computer Science and Engineering (AI & ML) student at SRM Institute of Science and Technology, Trichy Campus.</p></div>");
        html.append("<div class='team-card'><img src='/images/chethan.jpg'><h3>B Sai Chethan</h3><p>A passionate Computer Science and Engineering (AI & ML) student at SRM Institute of Science and Technology, Trichy Campus.</p></div>");
        html.append("<div class='team-card'><img src='/images/nikhil.jpg'><h3>B Sai Nikhil</h3><p>A passionate Computer Science and Engineering (AI & ML) student at SRM Institute of Science and Technology, Trichy Campus.</p></div>");
        html.append("</div>");

        html.append("<div class='guide-card'><h3>Dr. Naresh Kumar</h3><p>Faculty Guide<br>SRM Institute of Science and Technology</p></div>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private static String getCategoryPage(String category) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Category: " + category.toUpperCase() + "</title>");
        html.append("<style>");
        html.append(":root { --bg:#0b0c10; --text:#e1e1e1; --card:#1f2833; --primary:#66fcf1; }");
        html.append("[data-theme='light'] { --bg:#ffffff; --text:#0b0c10; --card:#f4f4f4; --primary:#0077cc; }");
        html.append("body{margin:0;padding:0;font-family:'Segoe UI',sans-serif;background:var(--bg);color:var(--text);} ");
        html.append(".container{width:90%;margin:auto;padding:40px 0;text-align:center;} ");
        html.append(".card{background:var(--card);color:var(--text);border-radius:15px;padding:20px;margin:20px auto;width:70%;box-shadow:0 10px 20px rgba(0,0,0,0.6);transition:transform 0.3s,box-shadow 0.3s;} ");
        html.append(".card:hover{transform:translateY(-8px);box-shadow:0 15px 30px rgba(0,0,0,0.7);} ");
        html.append(".card img{width:100%;height:200px;object-fit:contain;border-radius:12px;margin-bottom:15px;} ");
        html.append(".toggle-btn{position:fixed;top:10px;right:10px;padding:10px 20px;cursor:pointer;border:none;border-radius:8px;background:var(--primary);color:#fff;font-weight:bold;} ");
        html.append("@media(max-width:768px){.card{width:95%;}}");
        html.append("</style>");
        html.append("<script>function toggleTheme(){let theme=document.documentElement.getAttribute('data-theme');document.documentElement.setAttribute('data-theme',theme==='light'?'dark':'light');}</script>");
        html.append("</head><body data-theme='light'>");
        html.append("<button class='toggle-btn' onclick='toggleTheme()'>Toggle Dark/Light Mode</button>");
        html.append("<div class='container'><h2>Category: " + category.toUpperCase() + "</h2>");

        List<Map<String, String>> articles = fetchNews(category);
        if (articles.isEmpty()) {
            html.append("<p>No news available now. Please try later.</p>");
        } else {
            for (Map<String, String> article : articles) {
                html.append("<div class='card'>");
                if (!article.get("urlToImage").isEmpty()) {
                    html.append("<img src='" + article.get("urlToImage") + "'>");
                }
                html.append("<h2>" + article.get("title") + "</h2>");
                html.append("<p>" + article.get("description") + "</p>");
                html.append("<p><a href='" + article.get("url") + "' target='_blank' style='color:var(--primary);text-decoration:none;font-weight:bold;'>Read More</a></p>");
                html.append("<p><b>Date:</b> " + article.get("date") + "</p>");
                html.append("</div>");
            }
        }
        html.append("</div></body></html>");
        return html.toString();
    }

    private static List<Map<String, String>> fetchNews(String category) {
        List<Map<String, String>> articlesList = new ArrayList<>();
        try {
            String fromDate = new SimpleDateFormat("yyyy-MM-dd").format(
                    new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
            String urlStr = "https://newsapi.org/v2/everything?q=" + URLEncoder.encode(category, "UTF-8") +
                    "&from=" + fromDate + "&language=en&sortBy=publishedAt&pageSize=30&apiKey=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder jsonResponse = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonResponse.append(inputLine);
            }
            in.close();

            JsonObject json = JsonParser.parseString(jsonResponse.toString()).getAsJsonObject();
            JsonArray articles = json.getAsJsonArray("articles");

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

            for (JsonElement elem : articles) {
                JsonObject a = elem.getAsJsonObject();
                String title = a.has("title") && !a.get("title").isJsonNull() ? a.get("title").getAsString() : "No Title";
                String description = a.has("description") && !a.get("description").isJsonNull() ? a.get("description").getAsString() : "No description available.";
                if (description.split("\\.").length < 4) {
                    description += ". Stay updated with the latest news on " + title + ".";
                }
                String dateStr = a.has("publishedAt") ? a.get("publishedAt").getAsString() : "";
                String formattedDate = dateStr;
                try { formattedDate = outputFormat.format(inputFormat.parse(dateStr)); } catch (Exception ignored) {}

                String urlToImage = a.has("urlToImage") && !a.get("urlToImage").isJsonNull() ? a.get("urlToImage").getAsString() : "";
                String urlToArticle = a.has("url") && !a.get("url").isJsonNull() ? a.get("url").getAsString() : "#";

                Map<String, String> articleData = new HashMap<>();
                articleData.put("title", title);
                articleData.put("description", description);
                articleData.put("date", formattedDate);
                articleData.put("urlToImage", urlToImage);
                articleData.put("url", urlToArticle);

                articlesList.add(articleData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesList;
    }
} 