/**** Functionality in JS, but not in Java ********
 * functions (static methods of Global) to be called in Java
 */


/* String */
/*
anchor()
big()
blink()
bold()
fixed()
fontcolor()
fontsize()
italics()
link()
small()
strike()
sub()
sup()
*/

/* Number */
function toFixed(n, pos){
	return n.toFixed(pos);
}
/**** Functionality in Java, but not in JS ********
 * methods added to JS prototypes
 */

/* String */
/*
codePointAt(int)
codePointBefore(int)
codePointCount(int, int)
compareTo(String)
compareToIgnoreCase(String)
contentEquals(StringBuffer)
endsWith(String)
equalsIgnoreCase(String)
getBytes()
getBytes(int, int, byte[], int)
getBytes(String)
getBytes(Charset)
getChars(char[], int)
getChars(int, int, char[], int)
matches(String)
regionMatches(boolean, int, String, int, int)
regionMatches(int, String, int, int)
replaceFirst(String, String)
replaceAll(String, String)
startsWith(String)
startsWith(String, int)
trim()
*/

/* Number */

/************* STJS helper functions ***************/
var stjs={};

stjs.define=function( _super, _constructor, proto, statics){
	var	hasSuper = typeof _super === "function",
		key;
	
	// there is a constructor to extend
	// let do Intermediate stuff
	if(hasSuper){
		function I(){};
		I.prototype	= _super.prototype;
		_constructor.prototype	= new I;
	}
	if(proto){
	
		// assign every method from proto instance
		for(key in proto)
			_constructor.prototype[key]	= proto[key];
		
		// some browser does not include constructor, toString, and valueOf keywords
		// inside a for in loop, even if these are explicitly declared
		if(proto.hasOwnProperty("toString"))
			_constructor.prototype.toString	= proto.toString;
		if(proto.hasOwnProperty("valueOf"))
			_constructor.prototype.valueOf	= proto.valueOf;
	};
	
	if(statics){		
		// assign every method from statics instance
		for(key in statics)
			_constructor[key]	= statics[key];
	};
	
	// if there is a super, we create
	// the prototype that will make magic happen
	if(hasSuper) {
		// this prototype accepts zero or more arguments
		// if the first argument is a nullable value (undefined, false, 0, "", null)
		// this function will call directly the constructor
		// while if name is a string, this function will call
		// super method with that name (if any, otherwise it will rightly raise an error)
		_constructor.prototype._super = function(name){
			var	_rem	= this._super,
				result;
			// to continue with inherited chain
			// the _super property has to be
			// setted as the one stored in parent constructor prototype
			this._super	= _super.prototype._super;
			// this is because inside super method we would like to be able
			// to use again the magic _super with parent constructor or method as well
			result		= (name ? _super.prototype[name] : _super).apply(this, Array.prototype.slice.call(arguments, 1));
			
			// after constructor or parent method execution
			// we have to set the original super to be able
			// to call this method another time
			this._super	= _rem;
			
			// operation result
			return	result;
		};
		
		//copy static properties
		// assign every method from proto instance
		for(key in _super)
			_constructor[key]	= _super[key];
	}

	// remember the correct constructor
	_constructor.prototype.constructor	= _constructor;

	// build package and assign
	
	return	_constructor;
};