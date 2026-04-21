<?php
header("Content-Type: application/json");
require_once __DIR__ . '/../service/ContactService.php';

if (!isset($_GET['keyword'])) {
    echo json_encode([]);
    exit;
}

$keyword = $_GET['keyword'];

$service = new ContactService();
$result = $service->search($keyword);

echo json_encode($result);
?>