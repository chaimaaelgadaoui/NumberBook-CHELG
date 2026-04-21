<?php
header("Content-Type: application/json");
require_once __DIR__ . '/../service/ContactService.php';

$service = new ContactService();
$result = $service->getAll();

echo json_encode($result);
?>