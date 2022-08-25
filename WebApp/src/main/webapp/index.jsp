<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Start Page</title>
</head>
<body>
<form action = "Access" method = "post" enctype = "multipart/form-data">
    <fieldset> <!-- login phase with private key upload -->
        <legend>authenticate with a key and a voterID</legend>
        <input type = "file" name = "key" size = "50" />
        <br>
        <input type = "text" name = "VoterID" placeholder="VoterID" size = "50" />
    </fieldset>
    <input type = "submit" value = "Access" />
</form>

</body>
</html>