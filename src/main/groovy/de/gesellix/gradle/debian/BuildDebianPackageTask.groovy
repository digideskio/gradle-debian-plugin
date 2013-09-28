package de.gesellix.gradle.debian

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.vafer.jdeb.Console
import org.vafer.jdeb.DataProducer
import org.vafer.jdeb.Processor
import org.vafer.jdeb.mapping.Mapper
import org.vafer.jdeb.producers.DataProducerFile
import org.vafer.jdeb.utils.MapVariableResolver

import static org.vafer.jdeb.Compression.GZIP

class BuildDebianPackageTask extends DefaultTask {

  static final String NAME = 'buildDeb'

  @InputFile
  File copyrightFile
  @InputFile
  File changelogFile
  @InputDirectory
  File controlDirectory
  @Input
  DataProducer[] dataProducers
  @OutputFile
  File outputFile

  BuildDebianPackageTask() {
  }

  @TaskAction
  def buildPackage() {
    assert copyrightFile?.exists()
    assert changelogFile?.exists()
    assert controlDirectory?.exists()
    assert outputFile

    def processor = new Processor([
                                      info: { msg -> logger.info(msg) },
                                      warn: { msg -> logger.warn(msg) }] as Console,
                                  new MapVariableResolver([
                                      name: "packagename",
                                      version: "42"]))

    dataProducers = dataProducers.toList() << new DataProducerChangelog(changelogFile, "/usr/share/doc/packagename/changelog.gz", [] as String[], [] as String[], [] as Mapper[])
    dataProducers = dataProducers.toList() << new DataProducerFile(copyrightFile, "/usr/share/doc/packagename/copyright", [] as String[], [] as String[], [] as Mapper[])
    def packageDescriptor = processor.createDeb(controlDirectory.listFiles(), dataProducers, outputFile, GZIP)
//    dataProducers = dataProducers.toList() << new DataProducerFile(changelogFile, "/usr/share/doc/test-name/changelog.gz", [] as String[], [] as String[], [] as Mapper[])
//    processor.createChanges(packageDescriptor, null, null, null, null, )
  }
}
