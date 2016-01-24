<?php
//$day = 15.1234;
//list($day,$month,$year) = shell_exec('sudo java -classpath .:classes:/opt/pi4j/lib/* countwater');
$page = $_SERVER['PHP_SELF'];
$sec = "5";
$myfile = fopen("waterdata.txt", "r") or die("Unable to open file!");
$total = fgets($myfile);
$day = fgets($myfile);
$month = fgets($myfile);
$year =fgets ($myfile);
fclose($myfile);
?>
<html>
	<head>
	<meta http-equiv="refresh" content="<?php echo $sec?>;URL= '<?php echo $page?>'"
	</head>

	<style type="text/css">
	body
	{
	background-image:url('water.jpg');
	}

	</style>
	<body>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
	<center>
	<table>
                <tr>
                <td><font size="20"><strong>Today you have used <?php echo $day/1000*2.25?> liters of water<strong></td> 
		</tr>
		<br>
                <tr>
                <td><font size="20"><strong>This month you have used <?php echo $month/1000*2.25?> liters of water</strong></td>
                </tr>
		<br>
		<tr>
                <td><font size="20"><strong>This year you have used <?php echo $year/1000*2.25?> liters of water</strong> </td>
                </tr>
        </table>
	</center>

	</body>
</html>

