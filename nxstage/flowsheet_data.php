<?php

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    header($_SERVER['SERVER_PROTOCOL'].' 501 Not Implemented');
    header('Content-Type: application/json');
    echo '{"error":"'.$_SERVER['REQUEST_METHOD'].' not implemented."}';
    exit();
}

$startDate = DateTime::createFromFormat('m/d/Y', $_GET['start']);
$endDate = DateTime:: createFromFormat('m/d/Y', $_GET['end']);

$filenames = scandir('data');
if ($filenames === false) {
    header($_SERVER['SERVER_PROTOCOL'].' 500 Internal Server Error');
    header('Content-Type: application/json');
    echo '{"error":"Could not read data directory."}';
    exit();
}

$data = array();
foreach ($filenames as $filename) {
    $filedate = DateTime::createFromFormat('Y-m-d.json', $filename);
    if ($filedate <= $endDate && $filedate >= $startDate)
        $data[] = file_get_contents("data/$filename");
}

$output = '['.implode(',', $data).']';
header('Content-Type: application/json');
echo $output;

?>
