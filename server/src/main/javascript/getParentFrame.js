getParentFrame = function (element) {
    var rect = element.getClientRects()[0];
    var res = {'x': rect.left, 'y': rect.top};

    var doc = element.ownerDocument;
    var win = doc.defaultView;
    var topWin = win.top;

    var parentFrame = function (doc) {
        var win = doc.defaultView;
        var parentWin = win.parent;
        var parentDoc = parentWin.document;
        var frames = parentDoc.querySelectorAll('iframe');
        for (var i = 0; i < frames.length; i++) {
            if (frames[i].contentDocument === doc) {
                var r = frames[i];
                return r;
            }
        }
        return null;
    }

    while (win !== topWin) {
        rect = parentFrame(doc).getClientRects()[0];
        var xoff = rect.left;
        var yoff = rect.top;
        if (xoff > 0) {
            res.x += xoff;
        }
        if (yoff > 0) {
            res.y += yoff;
        }
        win = win.parent;
        doc = win.document;
    }
    return res;
}