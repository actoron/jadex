// Jadex Docs buildfile: nothing to build as docs are purely static
// But use markdown-lint for quality checking (especially fro finding broken links)
// Cf. https://github.com/appmattus/markdown-lint/ 
//
// Run with '..\gradlew markdownlint'
plugins {
  id("com.appmattus.markdown") version "0.6.0"
}

import com.appmattus.markdown.rules.*

markdownlint {
	rules {
		+ValidRelativeImagesRule {
			active	= false
		}

		// Disable some rules by setting active to false
		+MissingLinkSchemeRule {
			active	= false
		}
		+ValidRelativeLinksRule {
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
		+ListMarkerSpaceRule {
			active	= false
		}
		+BlanksAroundListsRule {
			active	= false
		}
		+BlanksAroundFencesRule {
			active	= false
		}
		+NoMultipleBlanksRule {
			active	= false
		}
		+NoEmphasisAsHeaderRule {
			active	= false
		}
		+NoTrailingSpacesRule {
			active	= false
		}
		+ConsistentUlStyleRule {
			active	= false
		}
		+ConsistentHeaderStyleRule {
			active	= false
		}
		+NoEmptyLinksRule {
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
		+NoSpaceInLinksRule {
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
		+HeaderIncrementRule {
			active	= false
		}
		+NoMissingSpaceAtxRule {
			active	= false
		}
    }

	reports {
		html()
	}
	
	threshold	= 0
}
