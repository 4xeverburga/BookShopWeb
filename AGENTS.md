# AGENTS — Running BookShopWeb

BookShopWeb is a Vietnamese-language online bookstore. A customer browses
categories and products, keeps a cart and a wishlist, places orders and
reviews the books they bought; an administrator manages users, categories,
products, orders and reviews under `/admin`.

Stack: plain servlets (`@WebServlet`) + JSP/JSTL views under
`src/main/webapp/WEB-INF/views/`, JDBI 3 over MySQL Connector/J 8, built as
a Maven WAR. The sources are Java 8.

| Thing | Where | Notes |
|---|---|---|
| **JDK 8** | `JAVA_HOME`; `java`, `javac`, `jar` on the path | |
| **Maven** | `mvn` on the path | |
| **Tomcat 9** | `$CATALINA_HOME` | `javax.servlet` 4.0; Tomcat 10+ moves to `jakarta.servlet` and will NOT work. |
| **MySQL 8.0** | `db:3306` (see below) | The dump relies on MySQL 8's `utf8mb4` defaults; on 5.7 it fails on the first Vietnamese string. |

---

## 1. The database

MySQL 8.0 at `db:3306`, user `root`, password `12345`, database
`bookshopdb`, already loaded with the schema and the seed rows of
`init/bookshopdb.sql`. Use that host, not `localhost`: the server is not
on this machine, only the `mysql` client is.

The dump creates the database itself, so reloading it means dropping it
first; that resets every table to its seeded state:

```bash
mysql -h db -uroot -p12345 -e 'DROP DATABASE IF EXISTS bookshopdb'
mysql -h db -uroot -p12345 < init/bookshopdb.sql
```

Seeded accounts — all with password `123` (stored as its MD5):

| username | role |
|---|---|
| `user1` | ADMIN |
| `user2`, `user3` | EMPLOYEE |
| `user4`, `user5` | CUSTOMER |

## 2. Build

```bash
mvn -B -q package          # -> target/BookShopWeb-1.0-SNAPSHOT.war
```

`-o` works offline once the dependencies are in `~/.m2`.

## 3. Product images

Product and category images are files on disk, not rows: the app reads and
writes them in one directory (`bookshop.image.path`, default
`/var/webapp/images`) and serves them at `/image/<name>`. The seed images
ship in `init/var-webapp-images.zip`:

```bash
mkdir -p /var/webapp/images
unzip -qo init/var-webapp-images.zip -d /var/webapp/images
```

## 4. Run

All links are built from the context path; deploy the WAR as
`BookShopWeb.war` so the app lives at `/BookShopWeb`:

```bash
rm -rf "$CATALINA_HOME/webapps/BookShopWeb"*
cp target/BookShopWeb-1.0-SNAPSHOT.war "$CATALINA_HOME/webapps/BookShopWeb.war"
CATALINA_OPTS="-Dbookshop.db.host=db" "$CATALINA_HOME/bin/catalina.sh" run
```

The app answers on <http://localhost:8080/BookShopWeb/> (the home page
lists products from the database); the admin console is
<http://localhost:8080/BookShopWeb/admin/signin>. Tomcat's log is on
stdout and in `$CATALINA_HOME/logs/`.

## 5. The config the app reads

`src/main/java/com/bookshopweb/utils/ConstantUtils.java` holds the
connection and image settings. Each one is a JVM system property with the
project's original value as its fallback, so a plain local setup needs no
flags at all:

| property | fallback |
|---|---|
| `bookshop.db.host` | `localhost` |
| `bookshop.db.port` | `3306` |
| `bookshop.db.name` | `bookshopdb` |
| `bookshop.db.user` | `root` |
| `bookshop.db.password` | `12345` |
| `bookshop.image.path` | `/var/webapp/images` |

Pass them through `CATALINA_OPTS` (`-Dname=value`).
