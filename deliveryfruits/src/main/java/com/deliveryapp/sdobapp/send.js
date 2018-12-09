function doGet(e) {
    handle(e);
}

function doPost(e) {
    handle(e);
}

function handle(request) {
    var comandName = request.parameter.comandName;
    var comandVal = request.parameter.comandVal;
    var dateName = request.parameter.dateName;
    var dateVal = request.parameter.dateVal;
    var produsName = request.parameter.produsName;
    var quantityName = request.parameter.quantityName;
    var amountName = request.parameter.amountName;
    var amountVal = request.parameter.amountVal;
    var id = request.parameter.id;

    var ss = SpreadsheetApp.openById(id);
    var sheet = ss.getActiveSheet();

    sheet.appendRow([comandName, comandVal]);
    sheet.appendRow([dateName, dateVal]);
    sheet.appendRow([produsName, quantityName, amountName]);
    sheet.appendRow([comandName, comandVal]);
    appendProducts(request, sheet);
    sheet.appendRow([" ", " ", amountVal]);
    sheet.appendRow([" ", " ", " "]);
}

function appendProducts(request, sheet) {
    var prod1 = request.parameter.prod1;
    var quant1 = request.parameter.quant1;

    if (prod1 != null && quant1 != null) {
        sheet.appendRow([prod1, quant1]);
    }

    var prod2 = request.parameter.prod2;
    var quant2 = request.parameter.quant2;

    if (prod2 != null && quant2 != null) {
        sheet.appendRow([prod2, quant2]);
    }

    var prod3 = request.parameter.prod3;
    var quant3 = request.parameter.quant3;

    if (prod3 != null && quant3 != null) {
        sheet.appendRow([prod3, quant3]);
    }

    var prod4 = request.parameter.prod4;
    var quant4 = request.parameter.quant4;

    if (prod4 != null && quant4 != null) {
        sheet.appendRow([prod4, quant4]);
    }

    var prod5 = request.parameter.prod5;
    var quant5 = request.parameter.quant5;

    if (prod5 != null && quant5 != null) {
        sheet.appendRow([prod5, quant5]);
    }

    var prod6 = request.parameter.prod6;
    var quant6 = request.parameter.quant6;

    if (prod6 != null && quant6 != null) {
        sheet.appendRow([prod6, quant6]);
    }

    var prod7 = request.parameter.prod7;
    var quant7 = request.parameter.quant7;

    if (prod7 != null && quant7 != null) {
        sheet.appendRow([prod7, quant7]);
    }

    var prod8 = request.parameter.prod8;
    var quant8 = request.parameter.quant8;

    if (prod8 != null && quant8 != null) {
        sheet.appendRow([prod8, quant8]);
    }

    var prod9 = request.parameter.prod9;
    var quant9 = request.parameter.quant9;

    if (prod9 != null && quant9 != null) {
        sheet.appendRow([prod9, quant9]);
    }

    var prod10 = request.parameter.prod10;
    var quant10 = request.parameter.quant10;

    if (prod10 != null && quant10 != null) {
        sheet.appendRow([prod10, quant10]);
    }

    var prod11 = request.parameter.prod11;
    var quant11 = request.parameter.quant11;

    if (prod11 != null && quant11 != null) {
        sheet.appendRow([prod11, quant11]);
    }

    var prod12 = request.parameter.prod12;
    var quant12 = request.parameter.quant12;

    if (prod12 != null && quant12 != null) {
        sheet.appendRow([prod12, quant12]);
    }

    var prod13 = request.parameter.prod13;
    var quant13 = request.parameter.quant13;

    if (prod13 != null && quant13 != null) {
        sheet.appendRow([prod13, quant13]);
    }

    var prod14 = request.parameter.prod14;
    var quant14 = request.parameter.quant14;

    if (prod14 != null && quant14 != null) {
        sheet.appendRow([prod14, quant14]);
    }

    var prod15 = request.parameter.prod15;
    var quant15 = request.parameter.quant15;

    if (prod15 != null && quant15 != null) {
        sheet.appendRow([prod15, quant15]);
    }

    var prod16 = request.parameter.prod16;
    var quant16 = request.parameter.quant16;

    if (prod16 != null && quant16 != null) {
        sheet.appendRow([prod16, quant16]);
    }

    var prod17 = request.parameter.prod17;
    var quant17 = request.parameter.quant17;

    if (prod17 != null && quant17 != null) {
        sheet.appendRow([prod17, quant17]);
    }

    var prod18 = request.parameter.prod18;
    var quant18 = request.parameter.quant18;

    if (prod18 != null && quant18 != null) {
        sheet.appendRow([prod18, quant18]);
    }

    var prod19 = request.parameter.prod19;
    var quant19 = request.parameter.quant19;

    if (prod19 != null && quant19 != null) {
        sheet.appendRow([prod19, quant19]);
    }

    var prod20 = request.parameter.prod20;
    var quant20 = request.parameter.quant20;

    if (prod20 != null && quant20 != null) {
        sheet.appendRow([prod20, quant20]);
    }
}