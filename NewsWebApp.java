import com.google.gson.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsWebApp {
    private static final String API_KEY = "9dd57f7234b64b9288524c29a065fe57";
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

        server.createContext("/about", exchange -> {
            try {
                String response = getAboutPage();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        });

        server.createContext("/privacy", exchange -> {
            try {
                String response = getPrivacyPage();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        });

        server.createContext("/contact", exchange -> {
            try {
                String response = getContactPage();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        });

        server.setExecutor(null);
        System.out.println("Server running at http://localhost:" + PORT);
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
    System.out.println(">>> LOADED NEW MAIN PAGE");
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
    html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    html.append("<title>Articlyst - News Aggregator</title>");
    html.append("<style>");
    html.append(":root { --bg:#0b0c10; --text:#e1e1e1; --card:#1f2833; --primary:#66fcf1; }");
    html.append("[data-theme='light'] { --bg:#ffffff; --text:#0b0c10; --card:#f4f4f4; --primary:#0077cc; }");
    html.append("body{margin:0;padding:0;font-family:'Segoe UI',sans-serif;background:var(--bg);color:var(--text);} ");
    html.append(".container{width:90%;margin:auto;padding:20px 0 40px 0;text-align:center;} ");
    html.append(".hero{padding:40px 20px;} .hero h1{font-size:3em;color:var(--primary);margin-bottom:10px;} ");
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
    html.append(".top-bar{display:flex;align-items:center;justify-content:space-between;padding:10px 20px;background:rgba(0,0,0,0.4);position:sticky;top:0;backdrop-filter:blur(6px);z-index:10;} ");
    html.append(".logo-text{font-weight:bold;font-size:1.2em;color:var(--primary);} ");
    html.append(".menu-btn{padding:8px 14px;border-radius:8px;border:none;background:var(--primary);color:#000;font-weight:bold;cursor:pointer;} ");
    html.append(".menu-panel{position:fixed;top:55px;right:15px;background:var(--card);border-radius:10px;box-shadow:0 10px 25px rgba(0,0,0,0.6);padding:10px 0;min-width:180px;display:none;z-index:20;} ");
    html.append(".menu-panel a{display:block;padding:10px 16px;color:var(--text);text-decoration:none;font-size:0.95em;text-align:left;} ");
    html.append(".menu-panel a:hover{background:rgba(255,255,255,0.08);} ");
    html.append(".footer-links{margin-top:40px;font-size:0.9em;}");
    html.append(".footer-links a{color:var(--primary);margin:0 8px;text-decoration:none;}");
    html.append(".footer-links a:hover{text-decoration:underline;}");
    html.append("@media(max-width:768px){.team-card,.category-card,.guide-card{width:90%;}.top-bar{padding:8px 14px;}}");
    html.append("</style>");
    html.append("<script>");
    html.append("function toggleTheme(){let theme=document.documentElement.getAttribute('data-theme');document.documentElement.setAttribute('data-theme',theme==='light'?'dark':'light');}");
    html.append("function toggleMenu(){var m=document.getElementById('menu-panel');if(m.style.display==='block'){m.style.display='none';}else{m.style.display='block';}}");
    html.append("</script>");
    html.append("</head><body data-theme='light'>");

    html.append("<div class='top-bar'>");
    html.append("<div class='logo-text'>Articlyst</div>");
    html.append("<button class='menu-btn' onclick='toggleMenu()'>Menu ☰</button>");
    html.append("</div>");

    html.append("<div id='menu-panel' class='menu-panel'>");
    html.append("<a href='/about'>About</a>");
    html.append("<a href='/privacy'>Privacy Policy</a>");
    html.append("<a href='/contact'>Contact</a>");
    html.append("</div>");

    html.append("<div class='container'>");

    html.append("<div class='hero'>");
    html.append("<h1>Articlyst News Aggregator</h1>");
    html.append("<p>🗞️ Welcome to Articlyst – Your Daily Dose of Real News!<br><br>"
            + "Tired of scrolling endlessly to find what’s happening around you?<br>"
            + "Articlyst brings everything you need — in one smart, simple, and beautiful place.<br><br>"
            + "We collect the latest and most trusted news from reliable sources and deliver it to you instantly. "
            + "From breaking national headlines to global events, technology trends, business updates, sports highlights, "
            + "health breakthroughs, and entertainment buzz — you’ll find it all here!<br><br>"
            + "Our goal is to make news reading easy, fast, and enjoyable.<br>"
            + "Every story on Articlyst is carefully selected and neatly presented so you can stay updated without wasting time. "
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

    html.append("<div class='footer-links'>");
    html.append("<a href='/about'>About</a> | ");
    html.append("<a href='/privacy'>Privacy Policy</a> | ");
    html.append("<a href='/contact'>Contact</a>");
    html.append("</div>");

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

    private static String getAboutPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<title>About Us - Articlyst</title>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:0;padding:20px;background:#0b0c10;color:#e1e1e1;}");
        html.append("h1{color:#66fcf1;}");
        html.append("a{color:#66fcf1;text-decoration:none;}");
        html.append("a:hover{text-decoration:underline;}");
        html.append(".container{max-width:900px;margin:0 auto;line-height:1.7;}");
        html.append("ul{margin-left:20px;}");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");
        html.append("<h1>About Articlyst</h1>");
        html.append("<p>Articlyst is a modern news aggregator designed to help you stay informed quickly and easily. ");
        html.append("Instead of searching multiple websites, you can browse trending topics, latest headlines, and important stories in one place.</p>");
        html.append("<p>Articlyst fetches news headlines and short descriptions from trusted external news providers ");
        html.append("and redirects users to the original publisher’s website to read the full article. ");
        html.append("We do not own or host the full content of these articles.</p>");
        html.append("<p>Our goal is to provide:</p>");
        html.append("<ul>");
        html.append("<li>Clean and distraction-free browsing of news categories</li>");
        html.append("<li>Fast access to breaking and trending stories</li>");
        html.append("<li>Respect for original publishers by redirecting to their websites</li>");
        html.append("</ul>");
        html.append("<p>Whether you are interested in India, world news, business, science, sports, health, ");
        html.append("or entertainment, Articlyst brings everything together in a simple and user-friendly interface.</p>");
        html.append("<p>If you have any questions or suggestions, feel free to contact us from the ");
        html.append("<a href='/contact'>Contact</a> page.</p>");
        html.append("<p>Back to <a href='/'>Home</a></p>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private static String getPrivacyPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<title>Privacy Policy - Articlyst</title>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:0;padding:20px;background:#0b0c10;color:#e1e1e1;}");
        html.append("h1{color:#66fcf1;}");
        html.append("h2{color:#66fcf1;margin-top:25px;}");
        html.append("a{color:#66fcf1;text-decoration:none;}");
        html.append("a:hover{text-decoration:underline;}");
        html.append(".container{max-width:900px;margin:0 auto;line-height:1.7;}");
        html.append("ul{margin-left:20px;}");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");
        html.append("<h1>Privacy Policy for Articlyst</h1>");
        html.append("<p>This Privacy Policy describes how Articlyst, accessible from <strong>articlyst.playwithchethan.com</strong>, ");
        html.append("handles information when you use our website.</p>");
        html.append("<h2>Information We Collect</h2>");
        html.append("<p>Articlyst does not ask users to create an account or submit personal details directly. ");
        html.append("However, like many websites, some non-personal information may be collected automatically, such as:</p>");
        html.append("<ul>");
        html.append("<li>IP address (may be logged by server or analytics tools)</li>");
        html.append("<li>Browser type and version</li>");
        html.append("<li>Device type and operating system</li>");
        html.append("<li>Pages visited and time spent on the site</li>");
        html.append("</ul>");
        html.append("<h2>Cookies and Third-Party Services</h2>");
        html.append("<p>We may use third-party services such as analytics or advertising networks that use cookies or similar technologies ");
        html.append("to collect information for usage statistics and personalized ads.</p>");
        html.append("<p>These third parties may use cookies to:</p>");
        html.append("<ul>");
        html.append("<li>Measure traffic and usage patterns on the site</li>");
        html.append("<li>Serve relevant advertisements</li>");
        html.append("</ul>");
        html.append("<p>You can control or disable cookies in your browser settings. ");
        html.append("Please refer to your browser’s help section for details.</p>");
        html.append("<h2>News Content and External Links</h2>");
        html.append("<p>Articlyst is a news aggregator. We display news headlines, short descriptions, and links ");
        html.append("that redirect you to the original publisher’s website to read the full article.</p>");
        html.append("<p>We are not responsible for the content, privacy policies, or practices of any third-party websites ");
        html.append("you visit from our links. We recommend reviewing the Privacy Policy of each website you visit.</p>");
        html.append("<h2>Data Security</h2>");
        html.append("<p>We aim to keep our systems reasonably secure. However, no method of transmission or storage ");
        html.append("over the internet is 100% secure, and we cannot guarantee absolute security.</p>");
        html.append("<h2>Changes to This Privacy Policy</h2>");
        html.append("<p>We may update this Privacy Policy from time to time. Any changes will be posted on this page, ");
        html.append("and the updated date may be revised accordingly.</p>");
        html.append("<h2>Contact Us</h2>");
        html.append("<p>If you have any questions about this Privacy Policy, you can contact us via the ");
        html.append("<a href='/contact'>Contact</a> page.</p>");
        html.append("<p>Back to <a href='/'>Home</a></p>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private static String getContactPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<title>Contact Us - Articlyst</title>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:0;padding:20px;background:#0b0c10;color:#e1e1e1;}");
        html.append("h1{color:#66fcf1;}");
        html.append("a{color:#66fcf1;text-decoration:none;}");
        html.append("a:hover{text-decoration:underline;}");
        html.append(".container{max-width:600px;margin:0 auto;line-height:1.7;}");
        html.append(".box{background:#1f2833;padding:20px;border-radius:8px;}");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");
        html.append("<h1>Contact Us</h1>");
        html.append("<div class='box'>");
        html.append("<p>If you have any questions, feedback, or suggestions about Articlyst, you can reach us at:</p>");
        html.append("<p><strong>Email:</strong> <a href='mailto:articlyst@gmail.com'>articlyst@gmail.com</a></p>");
        html.append("<p>We will do our best to respond as soon as possible.</p>");
        html.append("</div>");
        html.append("<p style='margin-top:20px;'>Back to <a href='/'>Home</a></p>");
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


