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

		// Allow same subsection name in different super sections (e.g. tutorial exercise "Start the Agent" sections)
		+NoDuplicateHeaderRule(allowDifferentNesting=true) {
		}

		// Currently broken by our figure captions layout
		+NoEmphasisAsHeaderRule {
			active	= false
		}
		
		// Currently broken by our SorryOutdated-snippets
		+FirstLineH1Rule {
			active	= false
		}
		
		
		// Disable some rules by setting active to false
		+ConsistentUlStyleRule {
			active	= false
		}
		+ConsistentHeaderStyleRule {
			active	= false
		}
		+UlIndentRule {
			active	= false
		}
		+SingleH1Rule {
			active	= false
		}
		+OlPrefixRule {
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

		// Rules we don't want to check? (What is our markdown coding style?)
		+NoWhitespaceFilenameRule {
			active	= false
		}
		+LineLengthRule {
			active	= false
		}
		+NoHardTabsRule {
			active	= false
		}
		+LowerCaseFilenameRule {
			active	= false
		}
		+NoBareUrlsRule {
			active	= false
		}
	}
	
	reports {
		html()
	}
	
	threshold	= 0
}
