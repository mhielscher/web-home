<?php

$entry = date("Y-m-d H:i:s")." - ".$_SERVER['REMOTE_ADDR']." [".$_SERVER['HTTP_USER_AGENT']."] Referer: [".$_SERVER['HTTP_REFERER']."]\n";
$count = file_put_contents("counter.log", $entry, FILE_APPEND);

header("Content-type: text/javascript");
echo "";

?>
