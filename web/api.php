<?php

set_time_limit(0);
error_reporting(0);

if (!isset($_POST["req"]) || !isset($_POST["cat"])) {
    echo json_encode([false, "İstek Düzğün Değil"], true);
    exit();
}

const BASE_URL = "http://"; //todo

$host = 'localhost';
$db = 'bakkal';
$user = 'root';
$pass = '';
$charset = 'utf8mb4';

$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES => false,
];
$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
try {
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (PDOException $e) {
    echo json_encode([false, "Sunucuda bir hata oluştu. Lütfen tekrar deneyin"]);
}

$isAdmin = false;

function encode($data)
{
    $search = ["'", '"', '`', '＂', '＇'];
    $encode = ["&apos;", "&quot;", "&specquot;", "&specquot1;", "&specquot2;"];

    for ($i = 0; $i < count($search); $i++) {
        $data = str_replace($search[$i], $encode[$i], $data); //todo check
    }

    return $data;
}

function decode($data)
{
    $encode = ["'", '"', '`', '＂', '＇'];
    $search = ["&apos;", "&quot;", "&specquot;", "&specquot1;", "&specquot2;"];

    for ($i = 0; $i < count($search); $i++) {
        $data = str_replace($search[$i], $encode[$i], $data); //todo check
    }

    return $data;
}

function clear($data)
{
    //$string = str_replace(' ', '-', $string); // Replaces all spaces with hyphens.
    //Türkçe karakter ı da problem todo  ş dede vr sorun
    $allow = "abcçdefgğhıijklmnoöprsştuüwxyzqABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜWĞXYZQP-_ 1234567890@.,:;+";

    for ($j = 0; $j < count($data); $j++) {
        $ok = false;
        for ($i = 0; $i < count($allow); $i++) {
            if ($data[$j] == $allow[$i]) {
                $ok = true;
                break;
            }
        }

        if ($ok == false) {
            $data[$i] = "";
        }
    }
//    $data = str_replace($allow[$i], "", $data);

    return $data;
}

function base64_to_jpeg($base64_string, $output_file)
{
    $ifp = fopen($output_file, 'wb');

    // split the string on commas
    // $data[ 0 ] == "data:image/png;base64"
    // $data[ 1 ] == <actual base64 string>
    $data = explode(',', $base64_string);

    // we could add validation here with ensuring count( $data ) > 1
    fwrite($ifp, base64_decode($data[1]));

    fclose($ifp);

    return $output_file;
}

function generate($length)
{
    $list = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $listLength = strlen($list);

    $token = "";

    for ($i = 0; $i < $length; $i++) {
        $randomIndex = rand(0, $listLength - 1);
        $token .= $list[$randomIndex];
    }

    return $token;
}

function loginCheck()
{
    if (!isset($_POST["token_lock"]) || !isset($_POST["token_key"])) {
        return 0;
    }

    global $pdo;
    $stmt = $pdo->query("SELECT user_id, user_admin FROM user WHERE token_lock = '" . $_POST["token_lock"] . "' AND token_key ='" . $_POST["token_key"] . "'");

    $userId = 0;

    while ($row = $stmt->fetch()) {
        $userId = $row["user_id"];

        global $isAdmin;
        $isAdmin = $row["user_admin"] == 0 ? false : true;
    }

    return $userId;

}

function inputCheck($name, $message, $min, $max)
{
    if (!isset($_POST[$name]) || (strlen($_POST[$name]) == 0 && $min != 0)) {
        return [false, $message . " Boş Olamaz"];
    }

    if (($min > 0 && strlen($_POST[$name]) < $min) || ($max > 0 && strlen($_POST[$name]) > $max)) {
        return [false, $message . " " . $min . " Karakterden Kısa, " . $max . " Karakterden Uzun Olamaz"];
    }

    return [true];
}

function arrayCheck($checks)
{
    for ($i = 0; $i < count($checks); $i++) {
        $obj = $checks[$i];

        $result = inputCheck($obj[0], $obj[1], $obj[2], $obj[3]);

        if (!$result[0]) {
            return $result;
        }
    }

    return [true];
}


$req = $_POST["req"];
$cat = $_POST["cat"];

$userId = loginCheck();

echo json_encode(req($req, $cat), true);

function req($req, $cat)
{
    global $isAdmin;
    global $pdo;

    if ($cat == "product") {
        if ($req == "insert") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                    ["product_brand", "Marka", 3, 32],
                    ["product_name", "İsim", 1, 64],
                    ["product_desc", "Açıklama", 0, 1024],
                    ["product_image", "Resim", 0, 1024],
                    ["product_weight", "Ağırlık", 1, 32],
                    ["product_price", "Fiyat", 1, 32],
                    ["product_category", "Kategori", 1, 8]
                ]
            );

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("INSERT INTO product(product_brand, product_name, product_desc, product_image, product_weight, product_price, product_category) VALUES ('" . clear($_POST["product_brand"]) . "', '" . clear($_POST["product_name"]) . "', '" . encode($_POST["product_desc"]) . "', '" . clear($_POST["product_image"]) . "', '" . clear($_POST["product_weight"]) . "', '" . clear($_POST["product_price"]) . "', " . intval($_POST["product_category"]) . ")");

            if ($stmt->execute()) {
                return [true, "Ürün başarıyla eklendi", $pdo->lastInsertId()];
            } else {
                return [false, "Ürün Eklenirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "add_stock") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                    ["product_id", "Ürün", 1, 32],
                    ["product_stock", "Stok", 1, 0]
                ]
            );

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("UPDATE product SET product_stock = product_stock + " . clear($_POST["product_stock"]) . " WHERE product_id = " . intval($_POST["product_id"]));

            if ($stmt->execute()) {
                return [false, "Stok Güncellendi"];
            } else {
                return [false, "Stok Güncelelnemedi"];
            }
        } else if ($req == "del_stock") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                    ["product_id", "Ürün", 1, 32],
                    ["product_stock", "Stok", 1, 0]
                ]
            );

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("UPDATE product SET product_stock = product_stock - " . clear($_POST["product_stock"]) . " WHERE product_id = " . intval($_POST["product_id"]));

            if ($stmt->execute()) {
                return [false, "Stok Güncellendi"];
            } else {
                return [false, "Stok Güncelelnemedi"];
            }
        } else if ($req == "update") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                    ["product_brand", "Marka", 3, 32],
                    ["product_name", "İsim", 1, 64],
                    ["product_desc", "Açıklama", 0, 1024],
                    ["product_id", "Ürün", 1, 32],
                    ["product_weight", "Ağırlık", 1, 32],
                    ["product_price", "Fiyat", 1, 32],
                    ["product_category", "Kategori", 1, 8],
                    ["product_image", "Resim", 0, 1024]
                ]
            );

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("UPDATE product SET product_brand = '" . clear($_POST["product_brand"]) . "', product_name ='" . clear($_POST["product_name"]) . "', product_desc = '" . clear($_POST["product_desc"]) . "', product_weight ='" . clear($_POST["product_weight"]) . "', product_price ='" . clear($_POST["product_price"]) . "', product_category = " . intval($_POST["product_category"]) . ", product_image = '" . clear($_POST["product_image"]) . "' WHERE product_id = " . intval($_POST["product_id"]));

            if ($stmt->execute()) {
                return [true, "Ürün Başarıyla Güncellendi", $pdo->lastInsertId()];
            } else {
                return [false, "Ürün Güncellenirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "upload_image") {
            try {
                if (!$isAdmin) {
                    return [false, "Yetkiniz Yok"];
                }

                $result = arrayCheck([
                        ["product_id", "Ürün", 1, 32],
                        ["product_image", "Resim", 1, 0],
                    ]
                );

                if (!$result[0]) {
                    return $result;
                }

                $image = "images/product/" . md5(time() + $_POST["product_id"]) . ".jpeg";

                file_put_contents($image, base64_decode($_POST["product_image"]));

                $imageInfo = getimagesize($image);

                if ($imageInfo[0] < 128 || $imageInfo[0] > 521 || $imageInfo[1] < 128 || $imageInfo[1] > 521) {
                    return [false, "Ürün resmi 128px den küçük veya 512 pxden büyük olamaz"];
                }

                if (!($imageInfo["mime"] == "images/jpeg" || $imageInfo["mime"] == "images/jpg" || $imageInfo["mime"] == "images/png")) {
                    unset($image);
                    return [false, "Ürün Resmi Uygun Formatta Değil"];
                }

                $stmt = $pdo->prepare("UPDATE product SET product_image = '" . $image . "' WHERE product_id = " . intval($_POST["product_id"]));

                if ($stmt->execute()) {
                    return [true, "Ürün Resmi Başarıyla Güncellendi", $pdo->lastInsertId()];
                } else {
                    return [false, "Ürün Resmi Güncellenirken Sorunla Karşılaşıldı"];
                }
            } catch (Exception $e) {
                return [false, "Ürün Resmi Güncellenirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "delete") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                ["product_id", "Ürün", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("DELETE FROM product WHERE product_id = " . intval($_POST["product_id"]));

            if ($stmt->execute()) {
                return [true, "Ürün Başarıyla Silindi", $pdo->lastInsertId()];
            } else {
                return [false, "Ürün Silinirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "get") {
            $result = arrayCheck([
                ["product_id", "Ürün", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("SELECT * FROM product WHERE product_id = " . intval($_POST["product_id"]));

            if ($stmt->execute()) {
                return [true, "Ürün Getirildi", $stmt->fetchAll()];
            } else {
                return [false, "Ürün Getirilirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "select") {
            $result = arrayCheck([
                ["page", "Sayfa", 1, 32],
                ["count", "Sayı", 1, 32],
                ["order", "Sıralama", 0, 32], //id asc
                ["category", "Kategori", 0, 32],
                ["search", "Arama", 0, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $where = "";

            if (intval($_POST["category"]) > 0) {
                $where = " WHERE product_category = " . intval($_POST["category"]);
            } else if (clear($_POST["search"]) != "") {
                $where = " WHERE product_name LIKE '%" . clear($_POST["search"]) . "%' OR product_brand = '%" . clear($_POST["search"]) . "%' OR product_desc = '%" . clear($_POST["search"]) . "%'";
            }

            $order = "";

            if ($_POST["order"] != "") {
                $order = " ORDER BY " . $_POST["order"];
            }

            $page = "";

            if (intval($_POST["count"]) > 0) {
                $page = " LIMIT " . (intval($_POST["count"]) * intval($_POST["page"])) . "," . intval($_POST["count"]);
            }

            $stmt = $pdo->prepare("SELECT * FROM product " . $where . $order . $page);

            if ($stmt->execute() && $stmt->rowCount() > 0) {
                return [true, $stmt->fetchAll()];
            } else {
                return [false, "Ürünler Getirilirken Sorunla Karşılaşıldı"];
            }
        } else {
            return [false, "İstek Düğün Değil"];
        }
    } else if ($cat == "user") {
        if ($req == "register") {
            $result = arrayCheck([
                ["user_email", "Eposta", 3, 32],
                ["user_name", "İsim", 3, 32],
                ["user_surname", "Soyisim", 3, 32],
                ["user_password", "Şifre", 1, 32],
                ["user_password_repeat", "Şifre Tekrar", 1, 32],
                ["user_address", "Adres", 1, 128],
                ["user_phone", "Telefon", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            if ($_POST["user_password"] != $_POST["user_password_repeat"]) {
                return [false, "Şifreler Uyuşmuyor"];
            }

            if (!filter_var($_POST["user_email"], FILTER_VALIDATE_EMAIL)) {
                return [false, "Eposta Uygun Değil"];
            }

            $stmt = $pdo->prepare("SELECT user_id FROM user WHERE user_email = '" . clear($_POST["user_email"]) . "'");

            if (!$stmt->execute() || $stmt->rowCount() != 0) {
                return [false, "Eposta Adresi Kullanılıyor"];
            }

            $stmt = $pdo->prepare("INSERT INTO user (user_email, user_name, user_surname, user_address, user_phone, user_password) VALUES ('" . clear($_POST["user_email"]) . "', '" . clear($_POST["user_name"]) . "', '" . clear($_POST["user_surname"]) . "', '" . clear($_POST["user_address"]) . "', '" . clear($_POST["user_phone"]) . "', '" . sha1($_POST["user_password"]) . "')");

            if ($stmt->execute()) {
                return [true, "Başarıyla Kayıt Olundu", $pdo->lastInsertId()];
            } else {
                return [false, "Kayıt Olunurken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "login") {
            $result = arrayCheck([
                ["user_email", "Eposta", 3, 32],
                ["user_password", "Şifre", 1, 32],
            ]);

            if (!$result[0]) {
                return $result;
            }

            if (!filter_var($_POST["user_email"], FILTER_VALIDATE_EMAIL)) {
                return [false, "Eposta Uygun Değil"];
            }

            $stmt = $pdo->query("SELECT * FROM user WHERE user_email = '" . clear($_POST["user_email"]) . "' AND user_password = '" . sha1($_POST["user_password"]) . "'");

            if (!$stmt->execute() || $stmt->rowCount() == 0) {
                return [false, "Kullanıcı Adı veya Şire Yanlış"];
            }

            $token_lock = generate(128);
            $token_key = generate(128);

            $stmti = $pdo->prepare("UPDATE user SET token_lock  = '$token_lock', token_key = '$token_key' WHERE user_email = '" . clear($_POST["user_email"]) . "' AND user_password = '" . sha1($_POST["user_password"]) . "'");


            if ($stmti->execute()) {
                return [true, "Başarıyla Giriş Yapıldı", $stmt->fetch(), $token_lock, $token_key];
            } else {
                return [false, "Giriş Yapılırken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "login_check") {
            global $userId;
            return [$userId > 0, "ok"];
        } else {
            return [false, "İstek Düzğün Değil"];
        }
    } else if ($cat == "order") {
        if ($req == "insert") {
            try {
                global $userId;

                if ($userId == 0) {
                    return [false, "Önce Giriş Yapmalısın"];
                }

                $result = arrayCheck([
                    ["total_amount", "Toplam Tutar", 1, 32],
                    ["item_count", "İtem Sayısı", 1, 32],
                    ["item_json", "İtemler", 1, 0]
                ]);

                if (!$result[0]) {
                    return $result;
                }

                $stmt = $pdo->prepare("INSERT INTO orders (order_date, order_user, order_total) VALUES ('" . date("m-d-Y h:i") . "', " . $userId . "  , '" . clear($_POST["total_amount"]) . "')");

                if (!$stmt->execute()) {

                }

                $orderId = $pdo->lastInsertId();

                $json = json_decode($_POST["item_json"], true);

                $falseCount = 0;

                for ($i = 0; $i < count($json); $i++) {
                    $stmt = $pdo->prepare("INSERT INTO order_item (order_id, item, item_price, item_count) VALUES (" . $orderId . "," . intval($json[$i]["item_id"]) . " , (SELECT product_price FROM product WHERE product_id = " . intval($json[$i]["item_id"]) . "), '" . $json[$i]["item_count"] . "')");

                    if (!$stmt->execute()) {
                        $falseCount++;
                    }

                    $stmt = $pdo->prepare("UPDATE product SET product_stock = product_stock - " . clear($json[$i]["item_count"]) . " WHERE product_id = " . intval($json[$i]["item_id"]));

                    $stmt->execute();
                }

                if ($falseCount > 0) {
                    return [true, "Sipariş Oluşturuldu Fakat Bazı Öğeler Eklenemedi"];
                } else {
                    return [true, "Sipariş Başarıyla Oluşturuldu"];
                }
            } catch (Exception $e) {
                return [false, "Sipariş Oluşturulamadı"];
            }
        } else if ($req == "select") {
            global $userId;

            if ($userId == 0) {
                return [false, "Önce Giriş Yapmalısın"];
            }

            $result = arrayCheck([
                ["page", "Sayfa", 1, 32],
                ["count", "Sayı", 1, 32],
                ["status", "Durum", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $page = "";

            if (intval($_POST["count"]) > 0) {
                $page = " LIMIT " . (intval($_POST["count"]) * intval($_POST["page"])) . "," . intval($_POST["count"]);
            }

            $where = " WHERE order_user = " . $userId;

            if (intval($_POST["status"]) > -1) {
                $where = " AND order_status = " . intval($_POST["status"]);
            }

            $stmt = $pdo->prepare("SELECT * FROM (SELECT * FROM orders" . $where . $page . ") ords LEFT JOIN order_item USING(order_id) ORDER BY order_id DESC");

            if ($stmt->execute()) {
                return [true, "Siparişler Getirildi", $stmt->fetchAll()];
            } else {
                return [false, "Siparişler Getirilemedi"];
            }
        }
    } else if ($cat == "category") {
        if ($req == "insert") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                ["category_name", "İsim", 2, 32],
                ["category_father", "Üst Kategori", 0, 32],
                ["category_color", "Renk", 0, 32],
                ["category_image", "Resim", 0, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("INSERT INTO category (category_name, category_father, category_color, category_image) VALUES ('" . clear($_POST["category_name"]) . "', 0, '" . clear($_POST["category_color"]) . "', '" . clear($_POST["category_image"]) . "')");

            if ($stmt->execute()) {
                return [true, "Kategori Başarıyla Eklendi", $pdo->lastInsertId()];
            } else {
                return [false, "Kategori Eklenirken Sorunla Karşılaşıldı"];
            }
        } else if ($req == "update") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                ["category_id", "Kategori", 1, 32],
                ["category_name", "İsim", 2, 32]//,
                //["category_father", "Üst Kategori", 0, 32],
                //["category_color", "Renk", 0, 32],
                //["category_image", "Resim", 0, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("UPDATE category SET category_name = '" . clear($_POST["category_name"]) . "' WHERE category_id = " . intval($_POST["category_id"]));

            if ($stmt->execute()) {
                return [true, "Kategori Başarıyla Güncellendi", $pdo->lastInsertId()];
            } else {
                return [false, "Kategori Güncellenirken Bir Sorunla Karşılaşıldı"];
            }
        } else if ($req == "delete") {
            if (!$isAdmin) {
                return [false, "Yetkiniz Yok"];
            }

            $result = arrayCheck([
                ["category_id", "Kategori", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("DELETE FROM category WHERE category_id = " . intval($_POST["category_id"]));

            if ($stmt->execute()) {
                return [true, "Kategori Başarıyla Silindi"];
            } else {
                return [false, "Kategori Silinirken Bir Sorunla Karşılaşıldı"];
            }
        } else if ($req == "select") {
            $result = arrayCheck([
                ["count", "Sayı", 1, 32],
                ["order", "Sıralama", 0, 32], //id asc
                ["page", "Sayfa", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $order = "";

            if ($_POST["order"] != "") {
                $order = " ORDER BY " . clear($_POST["order"]);
            }

            $page = "";

            if (intval($_POST["count"]) > 0) {
                $page = " LIMIT " . (intval($_POST["count"]) * intval($_POST["page"])) . "," . intval($_POST["count"]);
            }

            $stmt = $pdo->query("SELECT * FROM category " . $order . $page);

            if ($stmt->execute()) {
                return [true, "Kategori Başarıyla Getirildi", $stmt->fetchAll()];
            } else {
                return [false, "Kategori Getirilirken Bir Sorunla Karşılaşıldı"];
            }
        } else if ($req == "get") {
            $result = arrayCheck([
                ["category_id", "Kategori", 1, 32]
            ]);

            if (!$result[0]) {
                return $result;
            }

            $stmt = $pdo->prepare("SELECT * FROM category WHERE category_id = " . intval($_POST["category_id"]));

            if ($stmt->execute() && $stmt->rowCount() > 0) {
                return [true, "Kategori Başarıyla Getirildi", $stmt->fetch()];
            } else {
                return [false, "Kategori Getirilirken Bir Sorunla Karşılaşıldı"];
            }
        } else {
            return [false, "İstek Düzğün Değil"];
        }
    } else if ($cat == "all") {
        if ($req == "upload_image") {
            try {
                if (!$isAdmin) {
                    return [false, "Yetkiniz Yok"];
                }

                $result = arrayCheck([
                        ["image", "Resim", 1, 0]
                    ]
                );

                if (!$result[0]) {
                    return $result;
                }

                $image = "images/" . md5(time()) . ".jpeg";

                file_put_contents($image, base64_decode($_POST["image"]));

                $imageInfo = getimagesize($image);

                if ($imageInfo[0] < 128 || $imageInfo[0] > 1024 || $imageInfo[1] < 128 || $imageInfo[1] > 1024) {
                    return [false, "Ürün resmi 128px den küçük veya 1024 pxden büyük olamaz"];
                }


                //|| $imageInfo["mime"] == "image/png   "
                if (!($imageInfo["mime"] == "image/jpeg" || $imageInfo["mime"] == "image/jpg")) {
                    unset($image);
                    return [false, "Ürün Resmi Uygun Formatta Değil"];
                }


                return [true, "Ürün Resmi Başarıyla Güncellendi", $image];
            } catch (Exception $e) {
                return [false, "Ürün Resmi Güncellenirken Sorunla Karşılaşıldı"];
            }
        } else {
            //todo
        }
    } else {
        //todo
    }
}