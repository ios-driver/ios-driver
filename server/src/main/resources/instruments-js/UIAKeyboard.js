/*
 * Copyright 2013 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

UIAKeyboard.prototype.typeString_original = UIAKeyboard.prototype.typeString;

UIAKeyboard.prototype.typeString = function (value) {

    var regularCharacters = "";
    for (var i = 0; i < value.length; i++) {
        var letter = value[i];
        var key = this.getSpecialKey(letter);

        if (key.special && key.offset != 0) {
            if (regularCharacters.length > 0) {
                this.typeString_original(regularCharacters);
                regularCharacters = "";
            }
            var root = UIAutomation.cache.get('1');
            var result = null;
            var criteria = {"l10n": "none",
                "expected": key.baseKey,
                "matching": "exact",
                "method": "name"};
            try {
                result = root.element(-1, criteria);
            } catch (err) {
                this.moreLetters();
                result = root.element(-1, criteria);
            }

            // iphone xoffset = 32 yoffset=-54
            log("result :" + JSON.stringify(key));

            var x = result.rect().origin.x + 16;
            var y = result.rect().origin.y + 21;

            //for (var i = 0; i < 4; i++) {
            //    key.offset = i;

            if (key.shift === true) {
                this.shift();
            }
            var toX = x + (key.offset * 32);
            var toY = y - 54;
            var wait = (key.offset + 2) * 0.5;

            UIATarget.localTarget().dragFromToForDuration({x: x, y: y}, {x: toX, y: toY}, wait);
            //}

        } else {
            regularCharacters += letter;
        }
    }
    if (regularCharacters.length > 0) {
        this.typeString_original(regularCharacters);
    }

}

UIAKeyboard.prototype.getSpecialKey = function (key) {
    return fr.find(key);
}

UIAKeyboard.prototype.moreLetters = function () {
    var root = UIAutomation.cache.get('1');
    var more = root.element(-1,
                            {"l10n": "none",
                                "expected": "more, letters",
                                "matching": "exact",
                                "method": "name"});
    more.tap();
}

UIAKeyboard.prototype.shift = function () {
    var root = UIAutomation.cache.get('1');
    var more = root.element(-1,
                            {"l10n": "none",
                                "expected": "shift",
                                "matching": "exact",
                                "method": "name"});
    more.tap();
}

UIAKeyboard.prototype.hide = function() {
    var root = UIAutomation.cache.get('1');
    var hide = root.element(-1,
        {"OR":[
            {"l10n":"none","expected":"Hide keyboard","matching":"exact","method":"name"},
            {"l10n":"none","expected":"Done","matching":"exact","method":"name"}
        ]});
    hide.tap();
}

function Mapping(keyboard) {
    this.keyboard = keyboard;
    this.mapping = {};
}

Mapping.prototype.add = function (letter, lower, capital) {
    this.mapping[letter] = {};
    this.mapping[letter].lower = lower;
    this.mapping[letter].capital = capital;
}

Mapping.prototype.find = function (asked) {

    var res = {};
    res.special = false;

    for (var letter in this.mapping) {
        for (var i = 0; i < this.mapping[letter].lower.length; i++) {
            if (asked === this.mapping[letter].lower[i]) {
                res.special = true;
                res.baseKey = letter;
                res.offset = i;
                res.shift = false;
                return res;
            }
        }

        for (var i = 0; i < this.mapping[letter].capital.length; i++) {
            if (asked === this.mapping[letter].capital[i]) {
                res.special = true;
                res.baseKey = letter;
                res.offset = i;
                res.shift = true;
                return res;
            }
        }
    }
    return res;
}

var fr = new Mapping("fr");
fr.add("c", ["c", "ç", "ć", "č"], ["C", "Ç", "Ć", "Č"]);
fr.add("e", ["e", "è", "é", "ê", "ë", "ē", "ė", "ę"], ["E", "È", "É", "Ê", "Ë", "Ē", "Ė", "Ę"]);




