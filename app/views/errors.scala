package views

import constants.Constants
import controllers.routes.HomeController
import scalatags.Text.all._

/** https://www.systeme-de-design.gouv.fr/elements-d-interface/modeles/page-d-erreurs */
object errors {

  private val default403HelpMessage = frag(
    "Si vous pensez qu’il s’agit d’une erreur, vous pouvez ",
    a(href := HomeController.contact.url)("contacter l’équipe A+"),
    "."
  )

  def public403(helpMessage: Frag = default403HelpMessage): Tag =
    publicError(
      headTitle = "Accès non autorisé - Administration+",
      h1Title = "Accès non autorisé",
      errorCode = "Erreur 403",
      apologyMessage =
        "Il semblerait que vous ayez tenté d’accéder à une partie sécurisée du site pour laquelle vous ne disposez pas des autorisations nécessaires.",
      helpMessage = helpMessage,
    )

  def public404(): Tag =
    publicError(
      headTitle = "Page non trouvée - Administration+",
      h1Title = "Page non trouvée",
      errorCode = "Erreur 404",
      apologyMessage =
        "La page que vous cherchez est introuvable. Veuillez nous excuser pour la gène occasionnée.",
      helpMessage = frag(
        "Si vous avez tapé l’adresse web dans le navigateur, vérifiez qu’elle est correcte. La page n’est peut-être plus disponible. ",
        br,
        "Dans ce cas, pour continuer votre visite vous pouvez consulter notre page d’accueil.",
        br,
        "Sinon ",
        a(href := HomeController.contact.url)("contactez l’équipe A+"),
        " pour être redirigé vers la bonne information. ",
        "Merci de préciser dans votre email l’adresse web qui a généré ce message d’erreur. "
      ),
      additionalElements = ul(cls := "fr-btns-group fr-btns-group--inline-md")(
        li(a(cls := "fr-btn", href := "/")("Page d’accueil"))
      )
    )

  def public500WithCode(publicErrorCode: Option[String] = None): Tag =
    public500(
      frag(
        "Essayez de rafraîchir la page ou de réessayer plus tard. Si le problème persiste, vous pouvez ",
        a(href := HomeController.contact.url)("contacter l’équipe A+"),
        publicErrorCode.map(code =>
          frag(
            " en fournissant le code d’erreur ",
            b(code),
          )
        ),
        "."
      )
    )

  def public500(helpMessage: Frag): Tag =
    publicError(
      headTitle = "Erreur inattendue - Administration+",
      h1Title = "Erreur inattendue",
      errorCode = "Erreur 500",
      apologyMessage =
        "Désolé, le service rencontre un problème. Celui-ci est probablement temporaire. ",
      helpMessage = helpMessage,
    )

  def public503(): Tag =
    publicError(
      headTitle = "Service indisponible - Administration+",
      h1Title = "Service indisponible",
      errorCode = "Erreur 503",
      apologyMessage =
        "Le service Administration+ rencontre un problème, nous travaillons pour le résoudre le plus rapidement possible. ",
      helpMessage = frag(
        "Merci de réessayer plus tard. Vous serez bientôt en mesure de réutiliser le service. ",
        "Si le problème venait à persister, vous pouvez contacter le support Administration+ à l’adresse ",
        Constants.supportEmail,
        "."
      ),
    )

  private def publicError(
      headTitle: String,
      h1Title: String,
      errorCode: String,
      apologyMessage: Frag,
      helpMessage: Frag,
      additionalElements: Frag = frag(),
  ): Tag =
    views.main.publicErrorLayout(
      headTitle,
      div(cls := "fr-container")(
        div(
          cls := "fr-my-7w fr-mt-md-12w fr-mb-md-10w fr-grid-row fr-grid-row--gutters fr-grid-row--middle fr-grid-row--center"
        )(
          div(cls := "fr-py-0 fr-col-12 fr-col-md-6")(
            h1(h1Title),
            p(cls := "fr-text--sm fr-mb-3w")(errorCode),
            p(cls := "fr-text--lead fr-mb-3w")(apologyMessage),
            p(cls := "fr-text--sm fr-mb-5w")(helpMessage),
            additionalElements
          )
        )
      )
    )

}
