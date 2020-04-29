// Jadex Docs buildfile: nothing to build as docs are purely static
// But use markdown-lint for quality checking (especially for checking relative links)
// Cf. https://github.com/appmattus/markdown-lint/ 
//
// Run with '..\gradlew markdownlint'
plugins {
  id("com.appmattus.markdown") version "0.6.0"
}

import com.appmattus.markdown.rules.*

markdownlint {

	// Optional - Include files using RegEx for ALL rules, default=".*" (i.e. all files in root project directory)
    //includes = listOf(".*/quickstart.*/.*")
    
	rules {

		// Rules to be fixed
		+NoEmphasisAsHeaderRule {
			active	= false
		}
		
		// Disable some rules by setting active to false
		+ListMarkerSpaceRule {
			active	= false
		}
		+BlanksAroundListsRule {
			active	= false
		}
		+BlanksAroundFencesRule {
			active	= false
		}
		+ConsistentUlStyleRule {
			active	= false
		}
		+ConsistentHeaderStyleRule {
			active	= false
		}
		+NoReversedLinksRule {
			active	= false
		}
		+BlanksAroundHeadersRule {
			active	= false
		}
		+UlIndentRule {
			active	= false
		}
		+SingleH1Rule {
			active	= false
		}
		+NoWhitespaceFilenameRule {
			active	= false
		}
		+FencedCodeLanguageRule {
			active	= false
		}
		+OlPrefixRule {
			active	= false
		}
		+SingleTrailingNewlineRule {
			active	= false
		}
		+NoDuplicateHeaderRule {
			active	= false
		}
		+NoInlineHtmlRule {
			active	= false
		}
		+ListIndentRule {
			active	= false
		}
		+UlStartLeftRule {
			active	= false
		}
		+NoTrailingPunctuationRule {
			active	= false
		}
		+ProperNamesRule {
			active	= false
		}
		+FirstLineH1Rule {
			active	= false
		}
		+NoBareUrlsRule {
			active	= false
		}
		+NoMultipleSpaceAtxRule {
			active	= false
		}
		+NoMissingSpaceAtxRule {
			active	= false
		}


		// Rules we don't want to check? (What is our markdown coding style?)
		+LineLengthRule {
			active	= false
		}
		+NoHardTabsRule {
			active	= false
		}
		+NoMultipleBlanksRule {
			active	= false
		}
		+NoTrailingSpacesRule {
			active	= false
		}
		+LowerCaseFilenameRule {
			active	= false
		}
	}
	
	reports {
		html()
	}
	
	threshold	= 0
}
