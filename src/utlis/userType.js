

export const UserTypeEnum = {
  GUEST: 0,      // 游客
  VERIFIED: 1,   // 实名用户
  STUDENT: 2     // 本校学生
};

export const UserTypeList = [
  { label: "游客", value: UserTypeEnum.GUEST },
  { label: "实名用户", value: UserTypeEnum.VERIFIED },
  { label: "本校学生", value: UserTypeEnum.STUDENT }
];

export const UserType = ["游客", "学生","教师","店主"]