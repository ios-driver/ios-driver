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

/**
 * type the value, but also takes care of special characters that appear on long touch, like é or è
 * for the E key.
 * @param value
 */
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

            if (key.shift === true) {
                this.shift();
            }
            var toX = x + (key.offset * 32);
            var toY = y - 54;
            var multiplier = key.offset
            if (multiplier < 0) {
                multiplier = multiplier * -1;
            }
            // this is slow! but it's the only way to accomplish this
            // touchAndHold doesn't allow you to chain an action (like drag or flick right afterwards)
            // so we must make sure our starting touch lasts at least ~0.4 second on the original key
            // in order for the extra keys to appear. BAH!
            // time per key = duration / keys
            // offset 1, means 2 keys (original and the next key over)
            // 0.4 = duration / (offset + 1)
            // for whatever reason if the offset is 1, it needs just a little bit more time.
            var wait = (Math.max(3, multiplier + 1)) * 0.4;
            UIATarget.localTarget().dragFromToForDuration({x: x, y: y}, {x: toX, y: toY}, wait);

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

/**
 * locate and click on the "more letters" button on the keyboard.
 */
UIAKeyboard.prototype.moreLetters = function () {
    var root = UIAutomation.cache.get('1');
    var more = root.element(-1,
                            {"l10n": "none",
                                "expected": "more, letters",
                                "matching": "exact",
                                "method": "name"});
    more.tap();
}

/**
 * tap on shift.
 */
UIAKeyboard.prototype.shift = function () {
    var root = UIAutomation.cache.get('1');
    var more = root.element(-1,
                            {"l10n": "none",
                                "expected": "shift",
                                "matching": "exact",
                                "method": "name"});
    more.tap();
}

/**
 * makes the keyboard disappear.
 */
UIAKeyboard.prototype.hide = function () {
    var root = UIAutomation.cache.get('1');
    var hide = root.element(-1,
                            {"OR": [
                                {"l10n": "none", "expected": "Hide keyboard", "matching": "exact", "method": "name"},
                                {"l10n": "none", "expected": "Done", "matching": "exact", "method": "name"}
                            ]});
    hide.tap();
}

function Mapping(keyboard) {
    this.keyboard = keyboard;
    this.mapping = {};
}

Mapping.prototype.add = function (letter, lower, capital) {
    var offsetDirectionRight = lower[0] == letter; // otherwise the keys show up to the left.
    for (var i = 0; i < lower.length; i++) {
        if (lower[i] == letter) {
            continue;
        }
        this.mapping[lower[i]] =
        {"special": true, "shift": false, "baseKey": letter, "offset": offsetDirectionRight ? i : (i
                                                                                                       - lower.length
            + 1) }
    }
    for (var i = 0; i < capital.length; i++) {
        if (capital[i] == letter) {
            continue;
        }
        this.mapping[capital[i]] =
        {"special": true, "shift": true, "baseKey": letter, "offset": offsetDirectionRight ? i : (i
                                                                                                      - capital.length
            + 1) }
    }
}

Mapping.prototype.find = function (asked) {
    var res = this.mapping[asked];
    if (res == undefined) {
        res = { 'special': false };
    }
    return res;
}

var fr = new Mapping("fr");
fr.add("a", ["a", "à", "á", "â", "ä", "æ", "ã", "å", "ā"],
       ["a", "À", "Á", "Â", "Ä", "Æ", "Ã", "Å", "Ā"]);
fr.add("c", ["c", "ç", "ć", "č"], ["c", "Ç", "Ć", "Č"]);
fr.add("e", ["e", "è", "é", "ê", "ë", "ē", "ė", "ę"], ["e", "È", "É", "Ê", "Ë", "Ē", "Ė", "Ę"]);
fr.add("i", ["ì", "į", "ī", "í", "ï", "î", "i"], ["Ì", "Į", "Ī", "Í", "Ï", "Î", "i"]);
fr.add("l", ["ł", "l"], ["Ł", "l"]);
fr.add("n", ["ń", "ñ", "n"], ["Ń", "Ñ", "n"]);
fr.add("o", ["õ", "ō", "ø", "œ", "ó", "ò", "ö", "ô", "o"],
       ["Õ", "Ō", "Ø", "Œ", "Ó", "Ò", "Ö", "Ô", "o"]);
fr.add("s", ["s", "ß", "ś", "š"], ["s", "Ś", "Š"]);
fr.add("u", ["ū", "ú", "ù", "ü", "û", "u"], ["Ū", "Ú", "Ù", "Ü", "Û", "u"]);
// uh-oh.. my phone keyboard only has one other character for y and it shows up in the opposite direction.
// not sure what to do about this, setting to the simulator version of 6.1 for now.
// freynaud : seems to be different default keyboard.
fr.add("y", ["y", "ŷ", "ÿ"], ["y", "Ŷ", "Ÿ"]);
fr.add("z", ["z", "ž", "ź", "ż"], ["z", "Ž", "Ź", "Ż"]);

