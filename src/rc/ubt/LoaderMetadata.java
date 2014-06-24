package rc.ubt;

public @interface LoaderMetadata {
	loader class expected to search and register classes on it's on

	there is no need to pass instances of perform similar actions

	groups and order will be handles by "conditional group annotation"
	
	
	each plugin have it's own classloader as far as i know
}
