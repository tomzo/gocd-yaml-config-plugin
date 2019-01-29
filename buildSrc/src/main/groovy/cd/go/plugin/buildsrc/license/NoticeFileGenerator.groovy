package cd.go.plugin.buildsrc.license

import com.github.jk1.license.ProjectData
import com.github.jk1.license.render.ReportRenderer
import com.github.jk1.license.render.SingleInfoReportRenderer

class NoticeFileGenerator extends SingleInfoReportRenderer implements ReportRenderer {
    ReportRenderer toDecorate
    String licenseFolder

    NoticeFileGenerator(ReportRenderer toDecorate, String licenseFolder) {
        this.toDecorate = toDecorate;
        this.licenseFolder = licenseFolder;
    }

    @Override
    void render(ProjectData projectData) {
        toDecorate.render(projectData)

        projectData.allDependencies.collect { data ->
            def noticeFile = new File(licenseFolder + 'NOTICE.txt')
            if (!data.licenseFiles.empty) {
                data.licenseFiles.first().files.collect { file ->
                    if (new File(file).name.toLowerCase().contains("notice")) {
                        noticeFile.append(new File(licenseFolder + file).getText('UTF-8'))
                        noticeFile.append('\n')
                    }
                }
            }
        }
    }
}
