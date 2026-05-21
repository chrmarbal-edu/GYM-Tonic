package edu.gymtonic_app.ui.i18n

data class CommonStrings(
    val language: String,
    val back: String,
    val navTraining: String,
    val navChallenges: String,
    val navProfile: String,
    val homeSlogan: String,
    val homePresetWorkouts: String,
    val homeCreateRoutines: String,
    val homeTechnogym: String,
    val homeDiscounts: String,
    val homeChallenges: String,
    val homeClientArea: String,
    val homeGroup: String,
    val homeFriends: String,
    val weekTitle: String,
    val weeklyGoals: String,
    val myCalendar: String,
    val showMore: String,
    val achieved: String,
    val calendarDayNames: List<String>,
    val calendarMonthNames: List<String>
)

data class LoginStrings(
    val loginButton: String,
    val registerButton: String,
    val continueWith: String,
    val continueWithGoogle: String,
    val continueWithFacebook: String,
    val usernameLabel: String,
    val password: String,
    val forgotPassword: String,
    val enterButton: String,
    val noAccount: String,
    val signUpLink: String,
    val recoverTitle: String,
    val recoverEmailSent: String,
    val enterCode: String,
    val changePassSuccess: String,
    val errorExpired: String
)

data class RegisterStrings(
    val createAccount: String,
    val fullName: String,
    val usernameField: String,
    val email: String,
    val confirmPassword: String,
    val requiredField: String,
    val passwordsNoMatch: String,
    val nextButton: String,
    val alreadyHaveAccount: String,
    val loginLink: String,
    val birthDate: String,
    val birthDateFormat: String,
    val height: String,
    val weight: String,
    val selectGoal: String,
    val goalMaintenance: String,
    val goalLoseWeight: String,
    val goalBuildMuscle: String,
    val goalPerformance: String,
    val signUpButton: String
)

data class TrainingStrings(
    val trainingTitle: String,
    val trainingEmpty: String,
    val trainingCategoriesAvailable: (Int) -> String,
    val trainingRoutines: (Int) -> String,
    val trainingTapToOpen: String,
    val trainingNoWorkouts: String,
    val routineLoading: String,
    val routineWorkoutsTitle: String,
    val exercisesAvailable: (Int) -> String,
    val setsAndReps: String,
    val removeFavorite: String,
    val markFavorite: String,
    val exerciseTitle: String,
    val seconds: (Int) -> String,
    val createRoutineTitle: String,
    val basicInfo: String,
    val routineName: String,
    val routineDescription: String,
    val exercisesSection: String,
    val newExercise: (Int) -> String,
    val addExercise: String,
    val noExercisesAdded: String,
    val saveRoutine: String,
    val customizeSetsReps: String,
    val deleteExercise: String,
    val deleteRoutine: String,
    val deleteRoutineTitle: String,
    val deleteRoutineMessage: String,
    val deleteRoutineConfirm: String,
    val deleteRoutineCancel: String,
    val routineDeleted: String,
    val trainingGroupRoutines: String,
    val trainingMyRoutines: String
)

data class ProfileStrings(
    val profileTitle: String,
    val profileGreeting: (String) -> String,
    val profileDrawerTitle: String,
    val profileAccount: String,
    val profileAdjustes: String,
    val profileSignOut: String,
    val profileOpenSettings: String,
    val weeklyStreak: String,
    val viewWeek: String,
    val recentRoutines: String,
    val noRecentRoutines: String,
    val myGroups: String,
    val noGroups: String,
    val openLabel: String
)

data class GroupsStrings(
    val groupsTitle: String,
    val groupsDetailTitle: String,
    val groupsCreateTitle: String,
    val groupsCreateButton: String,
    val groupsCreateConfirm: String,
    val groupsCancel: String,
    val groupsNameLabel: String,
    val groupsDescriptionLabel: String,
    val groupsMySection: String,
    val groupsExploreSection: String,
    val groupsNoMoreToJoin: String,
    val groupsJoin: String,
    val groupsOpen: String,
    val groupsLeave: String,
    val groupsLeaveTitle: String,
    val groupsLeaveMessage: String,
    val groupsLeaveConfirm: String,
    val groupsAddRoutine: String,
    val groupsAddRoutineTitle: String,
    val groupsAddRoutineHint: String,
    val groupsRoutinesSection: String,
    val groupsNoRoutines: String,
    val groupsManage: String,
    val groupsSelectExercises: String,
    val groupsNoFavoriteExercises: String,
    val groupsShareRoutine: String,
    val groupsAdded: String,
    val groupsAdd: String,
    val groupsRoutineNameRequired: String,
    val groupsSelectExercisesRequired: String,
    val groupsMembersCount: (Int) -> String,
    val groupsPoints: (Int) -> String
)

data class SettingsStrings(
    val settingsTitle: String,
    val settingsNotifications: String,
    val settingsCompletedRoutines: String,
    val settingsWorkoutReminders: String,
    val settingsNewChallenges: String,
    val settingsAppearance: String,
    val settingsTheme: String,
    val settingsThemeSystem: String,
    val settingsThemeLight: String,
    val settingsThemeDark: String,
    val settingsUnits: String,
    val settingsWeight: String,
    val settingsDistance: String,
    val settingsMiles: String,
    val settingsLanguage: String,
    val settingsAppLanguage: String,
    val settingsLanguageSpanish: String,
    val settingsLanguageEnglish: String,
    val settingsAbout: String,
    val settingsVersion: String,
    val settingsSelectOption: String
)

data class AccountStrings(
    val accountTitle: String,
    val changePassword: String,
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
    val saveChanges: String,
    val changeEmail: String,
    val newEmail: String,
    val saveEmail: String,
    val twoFactorAuth: String,
    val twoFaEnabled: String,
    val twoFaDisabled: String,
    val twoFaDescription: String,
    val connectedAccounts: String,
    val accountConnected: String,
    val accountDisconnected: String,
    val connectButton: String,
    val disconnectButton: String,
    val deleteAccount: String,
    val deleteAccountWarning: String,
    val typeDeleteConfirmWord: String,
    val typeDeleteConfirmLabel: String,
    val deleteAccountButton: String
)

data class DiscountStrings(
    val discountsTitle: String,
    val discountsYourPoints: String,
    val discountsPointsSuffix: String,
    val discountsLoading: String,
    val discountsErrorGeneric: String,
    val discountsRetry: String,
    val discountsAvailableTitle: String,
    val discountsTierLabel: (Int) -> String,
    val discountsRequiredPoints: (Int) -> String,
    val discountsPointsToUnlock: (Int) -> String,
    val discountsUnlocked: String,
    val discountsClaim: String,
    val discountsCodeTitle: String,
    val discountsCodeMessage: String,
    val discountsCopyCode: String,
    val discountsCodeCopied: String,
    val discountsClose: String
)

data class FriendsStrings(
    val friendsTitle: String,
    val friendsTabFriends: String,
    val friendsTabIncoming: String,
    val friendsTabOutgoing: String,
    val friendsEmptyFriends: String,
    val friendsEmptyIncoming: String,
    val friendsEmptyOutgoing: String,
    val friendsAcceptAction: String,
    val friendsRejectAction: String,
    val friendsCancelAction: String,
    val friendsRemoveAction: String,
    val friendsAddTitle: String,
    val friendsAddFab: String,
    val friendsSearchHint: String,
    val friendsSendRequest: String,
    val friendsSearchEmpty: String,
    val friendsRequestSent: String,
    val friendsRequestAlready: String,
    val friendsAlreadyFriend: String,
    val friendsAcceptedMsg: String,
    val friendsRejectedMsg: String,
    val friendsCancelledMsg: String,
    val friendsRemovedMsg: String,
    val friendsLoadError: String
)

data class AdminStrings(
    val adminPanelSubtitle: String,
    val adminRoutines: String,
    val adminExercises: String,
    val adminUsers: String,
    val adminGroups: String,
    val adminMissions: String,
    val adminEmptyList: String,
    val adminRetry: String,
    val adminEdit: String,
    val adminCreate: String,
    val adminCancel: String,
    val adminDeleteConfirm: String,
    val adminDeleteTitle: String,
    val adminRoutineDetail: String,
    val adminUserDetail: String,
    val adminRole: String,
    val adminPoints: String,
    val adminExerciseType: String,
    val adminMissionName: String,
    val adminMissionType: String,
    val adminMissionObjective: String,
    val adminDeleteUserMessage: String,
    val adminDeleteExerciseMessage: String,
    val adminDeleteGroupMessage: String,
    val adminDeleteMissionMessage: String,
    val adminExerciseDetail: String,
    val adminMembersSection: String,
    val adminUploadVideo: String,
    val adminUploadImage: String,
    val adminOAuthProvider: String,
    val adminSelectType: String,
    val adminVideoSelected: String,
    val adminImageSelected: String,
    val adminExerciseName: String,
    val adminSearchHint: String,
    val adminAll: String
)

data class AppStrings(
    val common: CommonStrings,
    val login: LoginStrings,
    val register: RegisterStrings,
    val training: TrainingStrings,
    val profile: ProfileStrings,
    val groups: GroupsStrings,
    val settings: SettingsStrings,
    val account: AccountStrings,
    val discounts: DiscountStrings,
    val friends: FriendsStrings,
    val admin: AdminStrings
) {
    // Delegation getters to keep the old API
    val language get() = common.language
    val back get() = common.back
    val navTraining get() = common.navTraining
    val navChallenges get() = common.navChallenges
    val navProfile get() = common.navProfile
    val homeSlogan get() = common.homeSlogan
    val homePresetWorkouts get() = common.homePresetWorkouts
    val homeCreateRoutines get() = common.homeCreateRoutines
    val homeTechnogym get() = common.homeTechnogym
    val homeDiscounts get() = common.homeDiscounts
    val homeChallenges get() = common.homeChallenges
    val homeClientArea get() = common.homeClientArea
    val homeGroup get() = common.homeGroup
    val homeFriends get() = common.homeFriends
    val weekTitle get() = common.weekTitle
    val weeklyGoals get() = common.weeklyGoals
    val myCalendar get() = common.myCalendar
    val showMore get() = common.showMore
    val achieved get() = common.achieved
    val calendarDayNames get() = common.calendarDayNames
    val calendarMonthNames get() = common.calendarMonthNames

    val loginButton get() = login.loginButton
    val registerButton get() = login.registerButton
    val continueWith get() = login.continueWith
    val continueWithGoogle get() = login.continueWithGoogle
    val continueWithFacebook get() = login.continueWithFacebook
    val usernameLabel get() = login.usernameLabel
    val password get() = login.password
    val forgotPassword get() = login.forgotPassword
    val enterButton get() = login.enterButton
    val noAccount get() = login.noAccount
    val signUpLink get() = login.signUpLink
    val recoverTitle get() = login.recoverTitle
    val recoverEmailSent get() = login.recoverEmailSent
    val enterCode get() = login.enterCode
    val changePassSuccess get() = login.changePassSuccess
    val errorExpired get() = login.errorExpired

    val createAccount get() = register.createAccount
    val fullName get() = register.fullName
    val usernameField get() = register.usernameField
    val email get() = register.email
    val confirmPassword get() = register.confirmPassword
    val requiredField get() = register.requiredField
    val passwordsNoMatch get() = register.passwordsNoMatch
    val nextButton get() = register.nextButton
    val alreadyHaveAccount get() = register.alreadyHaveAccount
    val loginLink get() = register.loginLink
    val birthDate get() = register.birthDate
    val birthDateFormat get() = register.birthDateFormat
    val height get() = register.height
    val weight get() = register.weight
    val selectGoal get() = register.selectGoal
    val goalMaintenance get() = register.goalMaintenance
    val goalLoseWeight get() = register.goalLoseWeight
    val goalBuildMuscle get() = register.goalBuildMuscle
    val goalPerformance get() = register.goalPerformance
    val signUpButton get() = register.signUpButton

    val trainingTitle get() = training.trainingTitle
    val trainingEmpty get() = training.trainingEmpty
    val trainingCategoriesAvailable get() = training.trainingCategoriesAvailable
    val trainingRoutines get() = training.trainingRoutines
    val trainingTapToOpen get() = training.trainingTapToOpen
    val trainingNoWorkouts get() = training.trainingNoWorkouts
    val routineLoading get() = training.routineLoading
    val routineWorkoutsTitle get() = training.routineWorkoutsTitle
    val exercisesAvailable get() = training.exercisesAvailable
    val setsAndReps get() = training.setsAndReps
    val removeFavorite get() = training.removeFavorite
    val markFavorite get() = training.markFavorite
    val exerciseTitle get() = training.exerciseTitle
    val seconds get() = training.seconds
    val createRoutineTitle get() = training.createRoutineTitle
    val basicInfo get() = training.basicInfo
    val routineName get() = training.routineName
    val routineDescription get() = training.routineDescription
    val exercisesSection get() = training.exercisesSection
    val newExercise get() = training.newExercise
    val addExercise get() = training.addExercise
    val noExercisesAdded get() = training.noExercisesAdded
    val saveRoutine get() = training.saveRoutine
    val customizeSetsReps get() = training.customizeSetsReps
    val deleteExercise get() = training.deleteExercise
    val deleteRoutine get() = training.deleteRoutine
    val deleteRoutineTitle get() = training.deleteRoutineTitle
    val deleteRoutineMessage get() = training.deleteRoutineMessage
    val deleteRoutineConfirm get() = training.deleteRoutineConfirm
    val deleteRoutineCancel get() = training.deleteRoutineCancel
    val routineDeleted get() = training.routineDeleted
    val trainingGroupRoutines get() = training.trainingGroupRoutines
    val trainingMyRoutines get() = training.trainingMyRoutines

    val profileTitle get() = profile.profileTitle
    val profileGreeting get() = profile.profileGreeting
    val profileDrawerTitle get() = profile.profileDrawerTitle
    val profileAccount get() = profile.profileAccount
    val profileAdjustes get() = profile.profileAdjustes
    val profileSignOut get() = profile.profileSignOut
    val profileOpenSettings get() = profile.profileOpenSettings
    val weeklyStreak get() = profile.weeklyStreak
    val viewWeek get() = profile.viewWeek
    val recentRoutines get() = profile.recentRoutines
    val noRecentRoutines get() = profile.noRecentRoutines
    val myGroups get() = profile.myGroups
    val noGroups get() = profile.noGroups
    val openLabel get() = profile.openLabel

    val groupsTitle get() = groups.groupsTitle
    val groupsDetailTitle get() = groups.groupsDetailTitle
    val groupsCreateTitle get() = groups.groupsCreateTitle
    val groupsCreateButton get() = groups.groupsCreateButton
    val groupsCreateConfirm get() = groups.groupsCreateConfirm
    val groupsCancel get() = groups.groupsCancel
    val groupsNameLabel get() = groups.groupsNameLabel
    val groupsDescriptionLabel get() = groups.groupsDescriptionLabel
    val groupsMySection get() = groups.groupsMySection
    val groupsExploreSection get() = groups.groupsExploreSection
    val groupsNoMoreToJoin get() = groups.groupsNoMoreToJoin
    val groupsJoin get() = groups.groupsJoin
    val groupsOpen get() = groups.groupsOpen
    val groupsLeave get() = groups.groupsLeave
    val groupsLeaveTitle get() = groups.groupsLeaveTitle
    val groupsLeaveMessage get() = groups.groupsLeaveMessage
    val groupsLeaveConfirm get() = groups.groupsLeaveConfirm
    val groupsAddRoutine get() = groups.groupsAddRoutine
    val groupsAddRoutineTitle get() = groups.groupsAddRoutineTitle
    val groupsAddRoutineHint get() = groups.groupsAddRoutineHint
    val groupsRoutinesSection get() = groups.groupsRoutinesSection
    val groupsNoRoutines get() = groups.groupsNoRoutines
    val groupsManage get() = groups.groupsManage
    val groupsSelectExercises get() = groups.groupsSelectExercises
    val groupsNoFavoriteExercises get() = groups.groupsNoFavoriteExercises
    val groupsShareRoutine get() = groups.groupsShareRoutine
    val groupsAdded get() = groups.groupsAdded
    val groupsAdd get() = groups.groupsAdd
    val groupsRoutineNameRequired get() = groups.groupsRoutineNameRequired
    val groupsSelectExercisesRequired get() = groups.groupsSelectExercisesRequired
    val groupsMembersCount get() = groups.groupsMembersCount
    val groupsPoints get() = groups.groupsPoints

    val settingsTitle get() = settings.settingsTitle
    val settingsNotifications get() = settings.settingsNotifications
    val settingsCompletedRoutines get() = settings.settingsCompletedRoutines
    val settingsWorkoutReminders get() = settings.settingsWorkoutReminders
    val settingsNewChallenges get() = settings.settingsNewChallenges
    val settingsAppearance get() = settings.settingsAppearance
    val settingsTheme get() = settings.settingsTheme
    val settingsThemeSystem get() = settings.settingsThemeSystem
    val settingsThemeLight get() = settings.settingsThemeLight
    val settingsThemeDark get() = settings.settingsThemeDark
    val settingsUnits get() = settings.settingsUnits
    val settingsWeight get() = settings.settingsWeight
    val settingsDistance get() = settings.settingsDistance
    val settingsMiles get() = settings.settingsMiles
    val settingsLanguage get() = settings.settingsLanguage
    val settingsAppLanguage get() = settings.settingsAppLanguage
    val settingsLanguageSpanish get() = settings.settingsLanguageSpanish
    val settingsLanguageEnglish get() = settings.settingsLanguageEnglish
    val settingsAbout get() = settings.settingsAbout
    val settingsVersion get() = settings.settingsVersion
    val settingsSelectOption get() = settings.settingsSelectOption

    val accountTitle get() = account.accountTitle
    val changePassword get() = account.changePassword
    val currentPassword get() = account.currentPassword
    val newPassword get() = account.newPassword
    val confirmNewPassword get() = account.confirmNewPassword
    val saveChanges get() = account.saveChanges
    val changeEmail get() = account.changeEmail
    val newEmail get() = account.newEmail
    val saveEmail get() = account.saveEmail
    val twoFactorAuth get() = account.twoFactorAuth
    val twoFaEnabled get() = account.twoFaEnabled
    val twoFaDisabled get() = account.twoFaDisabled
    val twoFaDescription get() = account.twoFaDescription
    val connectedAccounts get() = account.connectedAccounts
    val accountConnected get() = account.accountConnected
    val accountDisconnected get() = account.accountDisconnected
    val connectButton get() = account.connectButton
    val disconnectButton get() = account.disconnectButton
    val deleteAccount get() = account.deleteAccount
    val deleteAccountWarning get() = account.deleteAccountWarning
    val typeDeleteConfirmWord get() = account.typeDeleteConfirmWord
    val typeDeleteConfirmLabel get() = account.typeDeleteConfirmLabel
    val deleteAccountButton get() = account.deleteAccountButton

    val discountsTitle get() = discounts.discountsTitle
    val discountsYourPoints get() = discounts.discountsYourPoints
    val discountsPointsSuffix get() = discounts.discountsPointsSuffix
    val discountsLoading get() = discounts.discountsLoading
    val discountsErrorGeneric get() = discounts.discountsErrorGeneric
    val discountsRetry get() = discounts.discountsRetry
    val discountsAvailableTitle get() = discounts.discountsAvailableTitle
    val discountsTierLabel get() = discounts.discountsTierLabel
    val discountsRequiredPoints get() = discounts.discountsRequiredPoints
    val discountsPointsToUnlock get() = discounts.discountsPointsToUnlock
    val discountsUnlocked get() = discounts.discountsUnlocked
    val discountsClaim get() = discounts.discountsClaim
    val discountsCodeTitle get() = discounts.discountsCodeTitle
    val discountsCodeMessage get() = discounts.discountsCodeMessage
    val discountsCopyCode get() = discounts.discountsCopyCode
    val discountsCodeCopied get() = discounts.discountsCodeCopied
    val discountsClose get() = discounts.discountsClose

    val friendsTitle get() = friends.friendsTitle
    val friendsTabFriends get() = friends.friendsTabFriends
    val friendsTabIncoming get() = friends.friendsTabIncoming
    val friendsTabOutgoing get() = friends.friendsTabOutgoing
    val friendsEmptyFriends get() = friends.friendsEmptyFriends
    val friendsEmptyIncoming get() = friends.friendsEmptyIncoming
    val friendsEmptyOutgoing get() = friends.friendsEmptyOutgoing
    val friendsAcceptAction get() = friends.friendsAcceptAction
    val friendsRejectAction get() = friends.friendsRejectAction
    val friendsCancelAction get() = friends.friendsCancelAction
    val friendsRemoveAction get() = friends.friendsRemoveAction
    val friendsAddTitle get() = friends.friendsAddTitle
    val friendsAddFab get() = friends.friendsAddFab
    val friendsSearchHint get() = friends.friendsSearchHint
    val friendsSendRequest get() = friends.friendsSendRequest
    val friendsSearchEmpty get() = friends.friendsSearchEmpty
    val friendsRequestSent get() = friends.friendsRequestSent
    val friendsRequestAlready get() = friends.friendsRequestAlready
    val friendsAlreadyFriend get() = friends.friendsAlreadyFriend
    val friendsAcceptedMsg get() = friends.friendsAcceptedMsg
    val friendsRejectedMsg get() = friends.friendsRejectedMsg
    val friendsCancelledMsg get() = friends.friendsCancelledMsg
    val friendsRemovedMsg get() = friends.friendsRemovedMsg
    val friendsLoadError get() = friends.friendsLoadError

    val adminPanelSubtitle get() = admin.adminPanelSubtitle
    val adminRoutines get() = admin.adminRoutines
    val adminExercises get() = admin.adminExercises
    val adminUsers get() = admin.adminUsers
    val adminGroups get() = admin.adminGroups
    val adminMissions get() = admin.adminMissions
    val adminEmptyList get() = admin.adminEmptyList
    val adminRetry get() = admin.adminRetry
    val adminEdit get() = admin.adminEdit
    val adminCreate get() = admin.adminCreate
    val adminCancel get() = admin.adminCancel
    val adminDeleteConfirm get() = admin.adminDeleteConfirm
    val adminDeleteTitle get() = admin.adminDeleteTitle
    val adminRoutineDetail get() = admin.adminRoutineDetail
    val adminUserDetail get() = admin.adminUserDetail
    val adminRole get() = admin.adminRole
    val adminPoints get() = admin.adminPoints
    val adminExerciseType get() = admin.adminExerciseType
    val adminMissionName get() = admin.adminMissionName
    val adminMissionType get() = admin.adminMissionType
    val adminMissionObjective get() = admin.adminMissionObjective
    val adminDeleteUserMessage get() = admin.adminDeleteUserMessage
    val adminDeleteExerciseMessage get() = admin.adminDeleteExerciseMessage
    val adminDeleteGroupMessage get() = admin.adminDeleteGroupMessage
    val adminDeleteMissionMessage get() = admin.adminDeleteMissionMessage
    val adminExerciseDetail get() = admin.adminExerciseDetail
    val adminMembersSection get() = admin.adminMembersSection
    val adminUploadVideo get() = admin.adminUploadVideo
    val adminUploadImage get() = admin.adminUploadImage
    val adminOAuthProvider get() = admin.adminOAuthProvider
    val adminSelectType get() = admin.adminSelectType
    val adminVideoSelected get() = admin.adminVideoSelected
    val adminImageSelected get() = admin.adminImageSelected
    val adminExerciseName get() = admin.adminExerciseName
    val adminSearchHint get() = admin.adminSearchHint
    val adminAll get() = admin.adminAll
}
