<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        <title>NxStage Home Hemo Treatment Flowsheets</title>
        <link rel="stylesheet" href="/css/normalize.min.css" type="text/css" />
        <link rel="stylesheet" href="flowsheet.css" type="text/css" />
        <script src="/js/vendor/modernizr-2.6.1-respond-1.1.0.min.js"></script>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
        <script src="//code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $("#from-date").datepicker();
                $("#to-date").datepicker();
            });
        </script>
    </head>
    <body>
        <h1>NxStage Treatment Flowsheets</h1>
        <nav>
            <form method="GET" id="date-range-form" name="date-range-form">
                <table><tr>
                <td><label for="from-date">From:</label></td>
                <td><input type="text" id="from-date" name="from-date" /></td>
                <td style="width: 1em;"> </td>
                <td><label for="to-date">To:</label></td>
                <td><input type="text" id="to-date" name="to-date" /></td>
                <noscript>
                    <td><input type="submit" id="submit-buttom" name="submit-button" value="Go" /></td>
                </noscript>
                </tr></table>
            </form>
        </nav>
    </body>
</html>
