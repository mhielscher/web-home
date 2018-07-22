<html>
	<head>
		<?php
			$filename = $_GET['file'];
		?>
		<title><?php echo $filename; ?></title>
		<link href="/pretty/prettify.css" type="text/css" rel="stylesheet" />
		<script type="text/javascript" src="/pretty/prettify.js"></script>
	</head>
<body onload="prettyPrint()">
<pre class="prettyprint">
<?php
	$code = htmlspecialchars(file_get_contents($filename));
	echo $code;
?>
</pre>
</body>
</html>
