<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link href="css/index.css" rel="stylesheet" type="text/css">
</head>
<body>
<form action = "Access" method = "post" enctype = "multipart/form-data">
    <fieldset> <!-- login phase with private key upload -->
        <legend>Authenticate with a key and a voterID</legend>
        <input type = "file" name = "key" size = "50" />
        <br>
        <input type = "text" name = "VoterID" placeholder="VoterID" size = "50" />
    </fieldset>
    <input type = "submit" value = "Access" />
</form>

<form action = "Admin" method = "post">
    <fieldset> <!-- login phase with private key upload -->
        <legend>Admin Login</legend>
        <input type = "name" name = "name" placeholder="Admin name"/>
        <br>
        <input type="password" name="password" placeholder="Admin password">
    </fieldset>
    <input type = "submit" value = "Admin Access" />
</form>

</body>
</html>