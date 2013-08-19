<?php

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    header($_SERVER['SERVER_PROTOCOL'].' 501 Not Implemented');
    header('Content-Type: application/json');
    echo '{"error":"'.$_SERVER['REQUEST_METHOD'].' not implemented."}';
    exit();
}

if (isset($_GET['end']))
    $endDate = DateTime::createFromFormat('m/d/Y', $_GET['end']);
else
    $endDate = new DateTime("now");

if (isset($_GET['start']))
    $startDate = DateTime::createFromFormat('m/d/Y', $_GET['start']);
else {
    $oneWeek = new DateInterval("P14D");
    $startDate = $endDate->sub($oneWeek);
}

$filenames = scandir('data');
if ($filenames === false) {
    header($_SERVER['SERVER_PROTOCOL'].' 500 Internal Server Error');
    header('Content-Type: application/json');
    echo '{"error":"Could not read data directory."}';
    exit();
}

$data = array();
foreach ($filenames as $filename) {
    $filedate = DateTime::createFromFormat('Y-m-d-D*', $filename);
    if ($filedate <= $endDate && $filedate >= $startDate) {
        $data[] = trim(file_get_contents("data/$filename"));
    }
}

$output = '['.implode(',', $data).']';
header('Content-Type: application/json');
echo $output;

?>
