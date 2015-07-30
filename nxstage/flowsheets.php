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
            var flowsheetTemplate;
            
            function getTemplate() {
                $.get(
                    "template.html",
                    {},
                    function (data, status) {
                        flowsheetTemplate = data;
                    },
                    "html"
                );
            }
            
            function getFlowsheets(startDate, endDate) {
                $.get(
                    "flowsheet_data.php",
                    {
                        start: startDate,
                        end: endDate
                    },
                    function (data, status) {
                        if (status == "success")
                            displayFlowsheets(data);
                        else
                            alert(status);
                    },
                    'json'
                );
            }
            
            function parseVariable(variable, data) {
                var keys = variable.split('.');
                var dataPiece = data;
                for (var i=0; i<keys.length; i++) {
                    dataPiece = dataPiece[keys[i]];
                    if (dataPiece === undefined) {
                        dataPiece = "";
                        break;
                    }
                }
                return datapiece;
            }
            
            var printPattern = /\{\{ ((\w|\d|\.|\[\d\])+) \}\}/gm;
            var forPattern = /\{\% for (\w+) in ((\w|\.)+) \%\}/gm;
            var endforPattern = /\{\% endfor \%\}/gm;
            function parseTemplate(template, data) {
                var parsed = template;
                var forMatches = Array();
                var endforMatches = Array();
                var forMatch = forPattern.exec(template);
                while (forMatch !== null) {
                    forMatches.push(forMatch);
                }
                var endforMatch = endforPattern.exec(template);
                while (endforMatch !== null) {
                    endforMatches.push(endforMatch);
                }
                if (forMatches.length !== endforMatches.length) {
                    console.log("Unclosed for loop in template.");
                    return template;
                }
                
                for (var i=0; i<forMatches.length; i++) {
                    forMatch = forMatches[i];
                    endforMatch = endforMatches[i];
                    console.log(forMatch['index'], endforMatch['index']);
                    var loopSegment = parsed.slice(forMatch['index']+forMatch[0].length, endforMatch['index']);
                    console.log(loopSegment);
                    var loopIterable = parseVariable(forMatch[2], data);
                    var parsedLoopSegment = loopSegment;
                    var fullParsedLoop = "";
                    var loopVar = {}
                    var loopCount = 0;
                    var loopPrintMatch = printPattern.exec(loopSegment);
                    while (loopPrintMatch !== null && loopCount < loopIterable.length) {
                        loopVar[forMatch[1]] = loopIterable[loopCount];
                        var dataPiece = parseVariable(loopPrintMatch[1], loopVar);
                        parsedLoopSegment = parsedLoopSegment.replace(loopPrintMatch[0], dataPiece);
                        fullParsedLoop += parsedLoopSegment;
                        loopPrintMatch = printPattern.exec(loopSegment);
                        loopCount++;
                        console.log(loopCount);
                    }
                    template = template.substr(0, forMatch['index']) + fullParsedLoop + template.substr(endforMatch['index']+endforMatch.length);
                    var forMatch = forPattern.exec(template);
                }
                
                var printMatch = printPattern.exec(template);
                while (printMatch !== null) {
                    var keys = printMatch[1].split('.');
                    var dataPiece = data;
                    for (var i=0; i<keys.length; i++) {
                        dataPiece = dataPiece[keys[i]];
                        if (dataPiece === undefined) {
                            dataPiece = "";
                            break;
                        }
                    }
                    parsed = parsed.replace(printMatch[0], dataPiece);
                    printMatch = printPattern.exec(template);
                }
                return parsed;
            }
            
            function displayFlowsheets(flowsheets) {
                var flowsheetHTML = new Array();
                var idx = -1;
                for (var date in flowsheets) {
                    flowsheetHTML[++idx] = parseTemplate(flowsheetTemplate, flowsheets[date]);
                }
                $('#flowsheet-section').html(flowsheetHTML.join("\n\n"));
            }
            
            $(document).ready(function () {
                getTemplate();
                //$('#flowsheet-section').html('');
                $("#from-date").datepicker({
                    onSelect: function (startDateText, inst) {
                        endDateText = $("#to-date").val();
                        getFlowsheets(startDateText, endDateText);
                    }
                });
                $("#to-date").datepicker({
                    onSelect: function (endDateText, inst) {
                        startDateText = $("#from-date").val();
                        getFlowsheets(startDateText, endDateText);
                    }
                });
                getFlowsheets('', '');
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
