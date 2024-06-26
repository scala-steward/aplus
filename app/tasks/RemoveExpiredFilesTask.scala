package tasks

import java.io.File
import java.nio.file.Files
import java.time.{Instant, ZonedDateTime}
import java.time.temporal.ChronoUnit.DAYS
import javax.inject.Inject
import models.EventType
import org.apache.pekko.actor.ActorSystem
import play.api.Configuration
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{EventService, FileService}

class RemoveExpiredFilesTask @Inject() (
    actorSystem: ActorSystem,
    configuration: Configuration,
    eventService: EventService,
    fileService: FileService,
)(implicit executionContext: ExecutionContext) {
  private val filesPath = configuration.underlying.getString("app.filesPath")

  private val filesExpirationInDays: Int =
    configuration.underlying.getString("app.filesExpirationInDays").toInt

  val startAtHour = 5
  val now: ZonedDateTime = java.time.ZonedDateTime.now() // Machine Time

  val startDate: ZonedDateTime =
    now.toLocalDate.atStartOfDay(now.getZone).plusDays(1).withHour(startAtHour)

  val initialDelay: FiniteDuration = java.time.Duration.between(now, startDate).getSeconds.seconds

  actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = initialDelay, delay = 24.hours)(
    new Runnable { override def run(): Unit = removeExpiredFiles() }
  )

  def removeExpiredFiles(): Unit = {
    val beforeDate = Instant.now().minus(filesExpirationInDays.toLong + 1, DAYS)
    eventService.logNoRequest(
      EventType.FilesDeletion,
      s"Début de la suppression des fichiers avant $beforeDate"
    )
    val dir = new File(filesPath)
    if (dir.exists() && dir.isDirectory) {
      val fileToDelete = dir
        .listFiles()
        .filter(_.isFile)
        .filter { file =>
          val instant = Files.getLastModifiedTime(file.toPath).toInstant
          instant.plus(filesExpirationInDays.toLong + 1, DAYS).isBefore(Instant.now())
        }
      fileToDelete.foreach(_.delete())
    }
    val _ = fileService.deleteBefore(beforeDate)
  }

}
