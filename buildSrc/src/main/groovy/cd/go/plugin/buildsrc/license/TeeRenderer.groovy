package cd.go.plugin.buildsrc.license

import com.github.jk1.license.ProjectData
import com.github.jk1.license.render.ReportRenderer
import com.github.jk1.license.render.SingleInfoReportRenderer

class TeeRenderer extends SingleInfoReportRenderer implements ReportRenderer {
    ReportRenderer toDecorate
    def LICENSES = [
            'Apache License, Version 2.0',
            'Apache 2.0',
            'The Apache Software License, Version 2.0',
            'New BSD License'
    ]

    TeeRenderer(ReportRenderer toDecorate) {
        this.toDecorate = toDecorate;
    }

    @Override
    void render(ProjectData projectData) {
        toDecorate.render(projectData)

        def violations = []

        projectData.allDependencies.collect { data ->

            def moduleDesc = "${data.group}:${data.name}:${data.version}"

            if (data.poms.empty) {
                violations << "POM file for ${moduleDesc} does not contain license information"
            }

            def pomData = data.poms.first()
            if (pomData.licenses.empty) {
                violations << "POM file for ${moduleDesc} does not contain license information"
            }

            def hasValidLicense = pomData.licenses.any { license -> LICENSES.contains(license.name) }
            if (!hasValidLicense) {
                violations << "Unsupported license '${pomData.licenses}', from module '${moduleDesc}'"
            }
        }

        if (!violations.empty) {
            throw new RuntimeException("There were the following errors with enforcing licensing\n${violations.collect { "\t${it}" }.join("\n")}")
        }
    }
}
