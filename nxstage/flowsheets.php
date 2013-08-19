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
            
            var printPattern = /\{\{ ((\w|\d|\.|\[\d\])+) \}\}/gm;
            var forPattern = /\{\% for (\w+) in ((\w|\.)+) \%\}/gm;
            function parseTemplate(template, data) {
                console.log(printPattern.exec(template));
                console.log(forPattern.exec(template));
                return template;
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
                flowsheetTemplate = $('#flowsheet-section').html();
                $('#flowsheet-section').html('');
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
<table border="0" cellspacing="0" cellpadding="0" class="ta1">
<colgroup><col width="92"/><col width="90"/><col width="114"/><col width="84"/><col width="80"/><col width="83"/><col width="90"/><col width="79"/><col width="76"/><col width="105"/><col width="123"/><col width="121"/><col width="99"/></colgroup>
<tr class="ro2">
<td colspan="11" style="text-align:left;width:0.8252in; " class="ce51"><p>{{ title }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr>
<tr class="ro3">
<td style="text-align:left;width:0.8252in; " class="ce52"><p>Name:</p></td>
<td colspan="2" style="text-align:left;width:0.8146in; " class="ce61"><p>{{ name }}</p></td>
<td style="text-align:left;width:0.761in; " class="ce67"><p>Date:</p></td>
<td style="text-align:left;width:0.7181in; " class="ce70"><p>{{ date }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce61"> </td>
<td style="text-align:left;width:0.8146in; " class="ce67"><p>Time:</p></td>
<td style="text-align:left;width:0.7075in; " class="ce64"><p>{{ time }}</p></td>
<td style="text-align:left;width:0.6862in; " class="ce61"> </td>
<td style="text-align:left;width:0.9429in; " class="ce61"> </td>
<td style="text-align:left;width:1.1043in; " class="ce69"> </td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro2"><td colspan="2" style="text-align:left;width:0.8252in; " class="ce53"><p>Pre-Treatment Data</p></td>
<td colspan="2" style="text-align:left;width:1.0291in; " class="ce66"><p>Type of dialysate:</p></td>
<td style="text-align:left;width:0.7181in; " class="ce62"><p>{{ pre_treatment.dialysate_type }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce62"> </td>
<td style="text-align:left;width:0.8146in; " class="ce62"> </td>
<td style="text-align:left;width:0.7075in; " class="ce73"> </td>
<td colspan="2" style="text-align:left;width:0.6862in; " class="ce74"><p>Cartridge Lot #</p></td>
<td style="text-align:left;width:1.1043in; " class="ce84"> </td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro4"><td style="text-align:left;width:0.8252in; " class="ce54"><p>Dry Weight</p></td>
<td style="text-align:left;width:0.8146in; " class="ce54"><p>Today's Weight</p></td>
<td style="text-align:left;width:1.0291in; " class="ce54"><p>UF Goal (incl. PO/IV)</p></td>
<td style="text-align:left;width:0.761in; " class="ce54"><p>Dialysate Liters</p></td>
<td style="text-align:left;width:0.7181in; " class="ce54"><p>Max FF</p></td>
<td style="text-align:left;width:0.75in; " class="ce54"><p>Standing BP/Pulse</p></td>
<td style="text-align:left;width:0.8146in; " class="ce54"><p>Sitting BP/Pulse</p></td>
<td style="text-align:left;width:0.7075in; " class="ce54"><p>Temp</p></td>
<td colspan="2" style="text-align:left;width:0.6862in; " class="ce75"><p>{{ pre_treatment.cartridge_lot }}</p></td>
<td style="text-align:left;width:1.1043in; " class="ce85"> </td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="ce91"> </td>
</tr><tr class="ro5"><td style="text-align:left;width:0.8252in; " class="ce55"><p>{{ pre_treatment.dry_weight }}</p></td>
<td style="text-align:left;width:0.8146in; " class="ce63"><p>{{ pre_treatment.weight }}</p></td>
<td style="text-align:left;width:1.0291in; " class="ce63"><p>{{ pre_treatment.UF_goal }}</p></td>
<td style="text-align:left;width:0.761in; " class="ce63"><p>{{ pre_treatment.dialysate_volume }}</p></td>
<td style="text-align:left;width:0.7181in; " class="ce63"><p>{{ pre_treatment.max_FF }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce63"><p>{{ pre_treatment.sitting_BP }}</p></td>
<td style="text-align:left;width:0.8146in; " class="ce63"><p>{{ pre_treatment.standing_BP }}</p></td>
<td style="text-align:left;width:0.7075in; " class="ce63"><p>{{ pre_treatment.temperature }}</p></td>
<td style="text-align:left;width:0.6862in; " class="ce76"><p>Drug</p></td>
<td style="text-align:left;width:0.9429in; " class="ce76"><p>Dose</p></td>
<td style="text-align:left;width:1.1043in; " class="ce76"><p>Time</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro3"><td colspan="2" style="text-align:left;width:0.8252in; " class="ce56"><p>Change in symptoms:</p></td>
<td colspan="6" style="text-align:left;width:1.0291in; " class="ce61"><p>{{ pre_treatment.symptoms_change }}</p></td>
<td style="text-align:left;width:0.6862in; " class="ce77"><p>{{ treatment.medications[0].medication }}:</p></td>
<td style="text-align:right; width:0.9429in; " class="ce81"><p>{{ treatment.medications[0].dose }}</p></td>
<td style="text-align:left;width:1.1043in; " class="ce86"><p>{{ treatment.medications[0].time }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro3"><td colspan="2" style="text-align:left;width:0.8252in; " class="ce56"><p>Change in medication:</p></td>
<td colspan="6" style="text-align:left;width:1.0291in; " class="ce61"><p>{{ pre_treatment.meds_change }}</p></td>
<td style="text-align:left;width:0.6862in; " class="ce77"><p>{{ treatment.medications[1].medication }}:</p></td>
<td style="text-align:right; width:0.9429in; " class="ce82"><p>{{ treatment.medications[1].dose }}</p></td>
<td style="text-align:left;width:1.1043in; " class="ce86"><p>{{ treatment.medications[1].time }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro3"><td style="text-align:left;width:0.8252in; " class="ce56"><p>Access:</p></td>
<td style="text-align:left;width:0.8146in; " class="ce61"><p>{{ pre_treatment.access_type }}</p></td>
<td style="text-align:left;width:1.0291in; " class="ce67"><p>Needle Gauge:</p></td>
<td style="text-align:left;width:0.761in; " class="ce67"><p>Arterial:</p></td>
<td style="text-align:left;width:0.7181in; " class="ce61"><p>{{ pre_treatment.needle_gauge.arterial }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce67"><p>Venous:</p></td>
<td style="text-align:left;width:0.8146in; " class="ce61"><p>{{ pre_treatment.needle_gauge.venous }}</p></td>
<td style="text-align:left;width:0.7075in; " class="ce69"> </td>
<td style="text-align:left;width:0.6862in; " class="ce77">{{ treatment.medications[2].medication }}:</td>
<td style="text-align:left;width:0.9429in; " class="ce82">{{ treatment.medications[2].dose }}</td>
<td style="text-align:left;width:1.1043in; " class="ce86">{{ treatment.medications[2].time }}</td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro3"><td colspan="2" style="text-align:left;width:0.8252in; " class="ce56"><p>Problems with access:</p></td>
<td colspan="6" style="text-align:left;width:1.0291in; " class="ce61">{{ pre_treatment.access_problems }}</td>
<td style="text-align:left;width:0.6862in; " class="ce77">{{ treatment.medications[3].medication }}:</td>
<td style="text-align:left;width:0.9429in; " class="ce81">{{ treatment.medications[3].dose }}</td>
<td style="text-align:left;width:1.1043in; " class="ce86">{{ treatment.medications[3].time }}</td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro6"><td style="text-align:left;width:0.8252in; " class="ce57"><p>Time</p></td>
<td style="text-align:left;width:0.8146in; " class="ce57"><p>BP/Pulse</p></td>
<td style="text-align:left;width:1.0291in; " class="ce57"><p>Dialysate Rate/Vol</p></td>
<td style="text-align:left;width:0.761in; " class="ce57"><p>UF Rate/Vol</p></td>
<td style="text-align:left;width:0.7181in; " class="ce57"><p>Blood Flow Rate</p></td>
<td style="text-align:left;width:0.75in; " class="ce57"><p>Venous Pressure</p></td>
<td style="text-align:left;width:0.8146in; " class="ce57"><p>Effluent Pressure</p></td>
<td style="text-align:left;width:0.7075in; " class="ce57"><p>Arterial Pressure</p></td>
<td style="text-align:left;width:0.6862in; " class="ce57"><p>FF</p></td>
<td colspan="2" style="text-align:left;width:0.9429in; " class="ce57"><p>Medication or bolus</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="ce92"> </td>
</tr>
{% for row in treatment.chart %}
<tr class="ro7">
<td style="text-align:left;width:0.8252in; " class="ce58"><p>{{ row.time }}</p></td>
<td style="text-align:left;width:0.8146in; " class="ce58"><p>{{ row.BP }}</p></td>
<td style="text-align:left;width:1.0291in; " class="ce58"><p>{{ row.dialysate_rate }} / {{ row.dialysate_volume }}</p></td>
<td style="text-align:left;width:0.761in; " class="ce58"><p>{{ row.UF_rate }} / {{ row.UF_volume }}</p></td>
<td style="text-align:left;width:0.7181in; " class="ce58"><p>{{ row.blood_flow_rate }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce58"><p>{{ row.venous_pressure }}</p></td>
<td style="text-align:left;width:0.8146in; " class="ce58"><p>{{ row.effluent_pressure }}</p></td>
<td style="text-align:left;width:0.7075in; " class="ce58"><p>{{ row.arterial_pressure }}</p></td>
<td style="text-align:left;width:0.6862in; " class="ce58"><p>{{ row.FF }}</p></td>
<td colspan="2" style="text-align:left;width:0.9429in; " class="ce83">{{ row.med_bolus }}</td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr>
{% endfor %}
<tr class="ro2">
<td style="text-align:left;width:0.8252in; " class="ce59"><p>Total TX time:</p></td>
<td style="text-align:left;width:0.8146in; " class="ce64"><p>{{ treatment.total_time }}</p></td>
<td colspan="2" style="text-align:left;width:1.0291in; " class="ce56"><p>Total dialysate volume:</p></td>
<td style="text-align:left;width:0.7181in; " class="ce64"><p>{{ treatment.total_dialysate_volume }}</p></td>
<td style="text-align:left;width:0.75in; " class="ce56"><p>Total UF:</p></td>
<td style="text-align:left;width:0.8146in; " class="ce64"><p>{{ treatment.total_UF }}</p></td>
<td style="text-align:left;width:0.7075in; " class="ce56"><p>BLP:</p></td>
<td style="text-align:left;width:0.6862in; " class="ce64"><p>{{ treatment.BLP }}</p></td>
<td style="text-align:left;width:0.9429in; " class="ce56"><p>Total PO/IV:</p></td>
<td style="text-align:left;width:1.1043in; " class="ce88"><p>{{ treatment.total_PO_IV }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro3"><td colspan="4" style="text-align:left;width:0.8252in; " class="ce56"><p>Post-Treatment Data</p></td>
<td style="text-align:left;width:0.7181in; " class="Default"> </td>
<td style="text-align:left;width:0.75in; " class="Default"> </td>
<td style="text-align:left;width:0.8146in; " class="Default"> </td>
<td style="text-align:left;width:0.7075in; " class="Default"> </td>
<td style="text-align:left;width:0.6862in; " class="Default"> </td>
<td style="text-align:left;width:0.9429in; " class="Default"> </td>
<td style="text-align:left;width:1.1043in; " class="ce89"> </td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro4"><td style="text-align:left;width:0.8252in; " class="ce57"><p>Sitting BP/Pulse</p></td>
<td style="text-align:left;width:0.8146in; " class="ce57"><p>Standing BP/Pulse</p></td>
<td style="text-align:left;width:1.0291in; " class="ce57"><p>Temp</p></td>
<td style="text-align:left;width:0.761in; " class="ce57"><p>Weight</p></td>
<td colspan="4" style="text-align:left;width:0.7181in; " class="ce56"><p>Problems/complaints during treatment:</p></td>
<td colspan="3" style="text-align:left;width:0.6862in; " class="ce78"><p>{{ post_treatment.problems }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro8"><td style="text-align:left;width:0.8252in; " class="ce58"><p>{{ post_treatment.sitting_BP }}</p></td>
<td style="text-align:left;width:0.8146in; " class="ce58"><p>{{ post_treatment.standing_BP }}</p></td>
<td style="text-align:left;width:1.0291in; " class="ce58"><p>{{ post_treatment.temperature }}</p></td>
<td style="text-align:left;width:0.761in; " class="ce58"><p>{{ post_treatment.weight }}</p></td>
<td colspan="2" style="text-align:left;width:0.7181in; " class="ce71"><p>Dialyzer appearance:</p></td>
<td colspan="5" style="text-align:left;width:0.8146in; " class="ce72"><p>{{ post_treatment.dialyzer_appearance }}</p></td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr><tr class="ro10"><td style="text-align:left;width:0.8252in; " class="ce60"><p>Comments:</p></td>
<td colspan="10" style="text-align:left;width:0.8146in; " class="ce65">{{ post_treatment.comments }}</td>
<td style="text-align:left;width:1.0929in; " class="Default"> </td>
<td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr></table><table border="0" cellspacing="0" cellpadding="0" class="ta1"><colgroup><col width="99"/></colgroup><tr class="ro9"><td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr></table><table border="0" cellspacing="0" cellpadding="0" class="ta1"><colgroup><col width="99"/></colgroup><tr class="ro9"><td style="text-align:left;width:0.889in; " class="Default"> </td>
</tr>
</table>
        </section>
    </body>
</html>
