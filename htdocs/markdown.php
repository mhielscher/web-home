<?php

if (isset($_GET['raw'])) {
    $path = str_replace('../', '', $_SERVER['PATH_INFO']).'.md';
    $text = file_get_contents($_SERVER['DOCUMENT_ROOT'].$path);
    $html = $text;
    header("Content-type: text/plain");
    echo $text;
    exit;
}

# Install PSR-0-compatible class autoloader
spl_autoload_register(function($class){
        require '../lib/'.preg_replace('{\\\\|_(?!.*\\\\)}', DIRECTORY_SEPARATOR, ltrim($class, '\\')).'.php';
});

# Get Markdown class
use \Michelf\Markdown;

$path = str_replace('../', '', $_SERVER['PATH_INFO']).'.md';
$text = file_get_contents($_SERVER['DOCUMENT_ROOT'].$path);
$html = Markdown::defaultTransform($text);

?>
<!DOCTYPE html>
<html>
    <head>
        <title><?php echo basename($path);?> - Rendered Markdown</title>
    </head>
    <body>
                <?php
                        # Put HTML content in the document
                        echo $html;
                ?>
    </body>
</html>
