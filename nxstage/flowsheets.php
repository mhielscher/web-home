<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        <title>NxStage Home Hemo Treatment Flowsheets</title>
        <link rel="stylesheet" href="/css/normalize.min.css" type="text/css" />
        <link rel="stylesheet" href="flowsheet.css" type="text/css" />
        <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/ui-lightness/jquery-ui.css" type="text/css" />
        <script src="/js/vendor/modernizr-2.6.1-respond-1.1.0.min.js"></script>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
        <script src="//code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
        <script type="text/javascript">
            function getFlowsheets(startDate, endDate) {
                $.get(
                    "flowsheet_data.php",
                    {
                        start: startDate,
                        end: endDate
                    },
                    function (data, status, jqXHR) {
                        if (status == "success")
                            displayFlowsheets(data);
                        else
                            alert(status);
                    },
                    'json'
                );
            }
            
            function displayFlowsheets(flowsheets) {
                var flowsheetHTML = new Array();
                var idx = -1;
                for (var date in flowsheets) {
                    //flowsheetHTML[++idx] = '<table class="flowsheet" id="flowsheet-'.date."'>";
                    flowsheetHTML[++idx] = '<pre>';
                    flowsheetHTML[++idx] = flowsheets[date];
                    flowsheetHTML[++idx] = '</pre>';
                    //flowsheetHTML[++idx] = '</table>';
                }
                $('#flowsheet-section').html(flowsheetHTML.join(''));
            }
            
            $(document).ready(function () {
                $("#from-date").datepicker({
                    onSelect: function (startDateText, inst) {
                        endDateText = $("#to-date").val();
                        getFlowsheets(startDateText, endDateText);
                    }
                });
                $("#to-date").datepicker({
                    onSelect: function (endDateText, inst) {
                        startDateText = $("#from-date").val();
                        data = getFlowsheetData(startDateText, endDateText);
                        displayFlowsheets(data);
                    }
                });
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
        <hr>
        <section id="flowsheet-section">
        
        </section>
    </body>
</html>
