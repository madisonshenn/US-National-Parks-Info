<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<head>
    <title>U.S. National Parks</title>
</head>
<body>
<h1>U.S. National Parks</h1>
<p>Created by Madison Shen</p>
<form action="getNationalParksInfo" method="GET">
    <label for="parkCode"><strong>Parks:</strong></label>
    <br>
    <select name="parkCode" id="parkCode">
        <option value="acad">Acadia NP</option>
        <option value="cuva">Cuyahoga Valley NP</option>
        <option value="grsm">Great Smoky Mountains NP</option>
        <option value="maca">Mammoth Cave NP</option>
        <option value="nerid">New River Gorge NP</option>
        <option value="shen">Shenandoah NP</option>
    </select>
    <br><br>
    <input type="submit" value="Submit">
</form>
<script>
    function setParkName() {
        var dropdown = document.getElementById("parkCode");
        var selectedText = dropdown.options[dropdown.selectedIndex].text;
        document.getElementById("parkName").value = selectedText;
    }
</script>
</body>
