package modules

import com.typesafe.config.{Config, ConfigFactory}
import helper.{Crypto, UUIDHelper}
import java.util.UUID
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import scala.concurrent.duration._

// Wraps `configuration: play.api.Configuration`.
// We want `configuration` to be private.
@Singleton
class AppConfig @Inject() (configuration: Configuration) {

  val appSecret: String = configuration.get[String]("play.http.secret.key")

  val tokenExpirationInMinutes: Int =
    configuration.get[Int]("app.tokenExpirationInMinutes")

  val magicLinkSessionDurationInSeconds: Long =
    configuration.get[Long]("app.magicLinkSessionDurationInSeconds")

  val passwordSessionDurationInSeconds: Long =
    configuration.get[Long]("app.passwordSessionDurationInSeconds")

  val passwordRecoveryTokenExpirationInMinutes: Int =
    configuration.get[Int]("app.passwordRecoveryTokenExpirationInMinutes")

  val featureMandatSms: Boolean = configuration.get[Boolean]("app.features.smsMandat")

  val useLiveSmsApi: Boolean = configuration
    .getOptional[Boolean]("app.smsUseLiveApi")
    .getOrElse(false)

  val featureCanSendApplicationsAnywhere: Boolean =
    configuration.get[Boolean]("app.features.canSendApplicationsAnywhere")

  val featureAutoAddExpert: Boolean =
    configuration.get[Boolean]("app.features.autoAddExpert")

  val filesOvhS3AccessKey: String = configuration.get[String]("app.filesOvhS3AccessKey")
  val filesOvhS3SecretKey: String = configuration.get[String]("app.filesOvhS3SecretKey")
  val filesOvhS3Endpoint: String = configuration.get[String]("app.filesOvhS3Endpoint")
  val filesOvhS3Region: String = configuration.get[String]("app.filesOvhS3Region")
  val filesOvhS3Bucket: String = configuration.get[String]("app.filesOvhS3Bucket")

  val filesCurrentEncryptionKeyId: String =
    configuration.get[String]("app.filesCurrentEncryptionKeyId")

  /** Each key has an id, the id of the key used to encrypt a file is stored in the database as file
    * metadata.
    */
  val filesEncryptionKeys: Map[String, Crypto.Key] =
    configuration
      .get[String]("app.filesEncryptionKeys")
      .split(",")
      .map(_.split(":") match {
        case Array(id, key) => (id, Crypto.decodeKeyBase64(key))
        case _              =>
          throw new Exception(
            "Invalid `app.filesEncryptionKeys` format, should be of the form 'keyid1:<key base64 encoded>,keyid2:<key base64 encoded>'"
          )
      })
      .toMap

  val filesCurrentEncryptionKey: Crypto.Key =
    filesEncryptionKeys.get(filesCurrentEncryptionKeyId) match {
      case None =>
        throw new Exception(
          s"Cannot find current file encryption key '$filesCurrentEncryptionKeyId'"
        )
      case Some(key) => key
    }

  val topHeaderWarningMessage: Option[String] =
    configuration.getOptional[String]("app.topHeaderWarningMessage")

  val topHeaderConnectedPagesNoticeMessageHtml: Option[String] =
    configuration.getOptional[String]("app.topHeaderConnectedPagesNoticeMessageHtml")

  val topHeaderPublicPagesAlertMessageHtml: Option[String] =
    configuration.getOptional[String]("app.topHeaderPublicPagesAlertMessageHtml")

  val insecureAreasWithLoginByKey: List[UUID] = configuration
    .get[String]("app.areasWithLoginByKey")
    .split(",")
    .flatMap(UUIDHelper.fromString)
    .toList

  val clamAvIsEnabled: Boolean = configuration.get[Boolean]("app.clamav.enabled")

  val clamAvHost: String = configuration.get[String]("app.clamav.host")
  val clamAvPort: Int = configuration.get[Int]("app.clamav.port")
  val clamAvTimeout: FiniteDuration = configuration.get[Int]("app.clamav.timeoutInSeconds").seconds

  // This blacklist if mainly for experts who do not need emails
  // Note: be careful with the empty string
  val notificationEmailBlacklist: Set[String] =
    configuration
      .get[String]("app.notificationEmailBlacklist")
      .split(",")
      .map(_.trim)
      .filterNot(_.isEmpty)
      .toSet

  val defaultMailerConfig: Config = configuration.underlying.getObject("play.mailer").toConfig

  val emailPickersConfig: Option[Config] =
    configuration
      .getOptional[String]("app.mailer.pickersConfig")
      .map { raw =>
        ConfigFactory.parseString(raw)
      }

  val groupsWhichCannotHaveInstructors: Set[UUID] =
    configuration
      .get[String]("app.groupsWhichCannotHaveInstructors")
      .split(",")
      .map(_.trim)
      .filterNot(_.isEmpty)
      .flatMap(UUIDHelper.fromString)
      .toSet

  val anonymizedExportEnabled: Boolean = configuration.get[Boolean]("app.anonymizedExport.enabled")

  val statisticsNumberOfNewApplicationsUrl: Option[String] =
    configuration.getOptional[String]("app.statistics.numberOfNewApplicationsUrl")

  val statisticsPercentOfRelevantApplicationsUrl: Option[String] =
    configuration.getOptional[String]("app.statistics.percentOfRelevantApplicationsUrl")

  val statisticsPercentOfApplicationsByStatusUrl: Option[String] =
    configuration.getOptional[String]("app.statistics.percentOfApplicationsByStatusUrl")

  val statisticsBottomChartsUrls: List[String] =
    configuration
      .getOptional[String]("app.statistics.bottomChartsUrls")
      .toList
      .flatMap(
        _.split(",")
          .map(_.trim)
          .filterNot(_.isEmpty)
          .toList
      )

  val groupsWithDsfr: Set[UUID] =
    configuration
      .get[String]("app.groupsWithDsfr")
      .split(",")
      .map(_.trim)
      .filterNot(_.isEmpty)
      .flatMap(UUIDHelper.fromString)
      .toSet

  val zammadChatDomain: Option[String] =
    configuration.getOptional[String]("app.zammadChatDomain")

  val featureUserInactivityEmails: Boolean =
    configuration.get[Boolean]("app.features.userInactivityEmails")

  val userInactivityCronMinute: Int =
    configuration.get[Int]("app.userInactivityCronMinute")

  val userInactivityCronHour: Int =
    configuration.get[Int]("app.userInactivityCronHour")

  val userInactivityCronAdditionalDays: Int =
    configuration.get[Int]("app.userInactivityCronAdditionalDays")

  val userInactivityReminder1DelayInMinutes: Int =
    configuration.get[Int]("app.userInactivityReminder1DelayInMinutes")

  val userInactivityReminder2AdditionalDelayInMinutes: Int =
    configuration.get[Int]("app.userInactivityReminder2AdditionalDelayInMinutes")

  val userInactivityDeactivationAdditionalDelayInMinutes: Int =
    configuration.get[Int]("app.userInactivityDeactivationAdditionalDelayInMinutes")

}
