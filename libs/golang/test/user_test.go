package test

import (
	"fmt"
	"regexp"
	"strings"
	"testing"

	"github.com/google/uuid"
)

type UserStatus string

const (
	ACTIVE    UserStatus = "ACTIVE"
	SUSPENDED UserStatus = "SUSPENDED"
)

type Profile struct {
	Username  string
	AvatarUrl string
	Bio       string
}

type PasswordEncoder interface {
	Encode(string) string
	Matches(string, string) bool
}

type mockPasswordEncoder struct{}

func (m *mockPasswordEncoder) Encode(s string) string           { return "encoded:" + s }
func (m *mockPasswordEncoder) Matches(raw, encoded string) bool { return "encoded:"+raw == encoded }

// User结构体与Java一致
// 只实现部分核心字段和方法

type User struct {
	Id             uuid.UUID
	TenantId       uuid.UUID
	Email          string
	HashedPassword string
	Profile        Profile
	Status         UserStatus
}

func NewUser(tenantId uuid.UUID, email, hashedPassword string, profile Profile) *User {
	return &User{
		Id:             uuid.New(),
		TenantId:       tenantId,
		Email:          email,
		HashedPassword: hashedPassword,
		Profile:        profile,
		Status:         ACTIVE,
	}
}

// 错误类型需导出，便于反射和类型断言

type IllegalArgumentError struct{ msg string }

func (e *IllegalArgumentError) Error() string { return e.msg }

type IllegalStateError struct{ msg string }

func (e *IllegalStateError) Error() string { return e.msg }

func CreateUser(tenantId uuid.UUID, email, plainPassword, username string, encoder PasswordEncoder) (*User, error) {
	if email == "" {
		return nil, &IllegalArgumentError{"Email cannot be null or empty"}
	}
	if plainPassword == "" {
		return nil, &IllegalArgumentError{"Password cannot be null or empty"}
	}
	if username == "" {
		return nil, &IllegalArgumentError{"Username cannot be null or empty"}
	}
	if encoder == nil {
		return nil, &IllegalArgumentError{"PasswordEncoder cannot be null"}
	}
	// 邮箱格式校验
	emailRegex := `^[a-zA-Z0-9._%%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`
	if matched := regexp.MustCompile(emailRegex).MatchString(email); !matched {
		return nil, &IllegalArgumentError{"Invalid email format: " + email}
	}
	if len(plainPassword) < 6 {
		return nil, &IllegalArgumentError{"Password must be at least 6 characters long"}
	}
	profile := Profile{Username: username}
	return NewUser(tenantId, email, encoder.Encode(plainPassword), profile), nil
}

func (u *User) UpdateProfile(newProfile Profile) error {
	if newProfile.Username == "" {
		return &IllegalArgumentError{"Profile username cannot be null or empty"}
	}
	u.Profile = newProfile
	return nil
}

func (u *User) ChangePassword(newPassword string, encoder PasswordEncoder) error {
	if u.Status == SUSPENDED {
		return &IllegalStateError{"Cannot change password for a suspended user."}
	}
	if newPassword == "" {
		return &IllegalArgumentError{"New password cannot be null or empty"}
	}
	if len(newPassword) < 6 {
		return &IllegalArgumentError{"Password must be at least 6 characters long"}
	}
	u.HashedPassword = encoder.Encode(newPassword)
	return nil
}

func (u *User) Suspend() error {
	if u.Status == SUSPENDED {
		return fmt.Errorf("User is already suspended")
	}
	u.Status = SUSPENDED
	return nil
}

func (u *User) Activate() error {
	if u.Status == ACTIVE {
		return fmt.Errorf("User is already active")
	}
	u.Status = ACTIVE
	return nil
}

// UserTest 继承ExcelDrivenTest，实现参数构建

type UserTest struct {
	ExcelDrivenTest
}

// 业务相关参数获取逻辑全部放在这里
func (ut *UserTest) GetMethodParamValue(paramName string, caseData map[string]string) interface{} {
	paramName = strings.TrimSpace(paramName)
	if debugFlag {
		fmt.Printf("[DEBUG] paramName: '%s'\n", paramName)
	}
	var result interface{}
	switch paramName {
	case "plainPassword":
		result = caseData["password"]
	case "newPassword":
		result = caseData["newPassword"]
	case "encoder", "passwordEncoder":
		result = &mockPasswordEncoder{}
	case "newProfile", "profile":
		username := caseData["profile_username"]
		avatar := caseData["profile_avatarUrl"]
		bio := caseData["profile_bio"]
		if username == "" || avatar == "" || bio == "" {
			fmt.Printf("[WARNING] Profile字段缺失: username=%v, avatar=%v, bio=%v\n", username, avatar, bio)
		}
		result = Profile{
			Username:  username,
			AvatarUrl: avatar,
			Bio:       bio,
		}
	case "tenantId", "groupId", "roleId":
		v := caseData[paramName]
		if v != "" {
			tid, err := uuid.Parse(v)
			if err == nil {
				result = tid
			} else {
				result = uuid.UUID{}
			}
		} else {
			result = uuid.UUID{}
		}
	case "username":
		result = caseData["username"]
	case "email":
		result = caseData["email"]
	case "hashedPassword":
		result = caseData["hashedPassword"]
	case "status":
		if caseData[paramName] == "SUSPENDED" {
			result = SUSPENDED
		} else {
			result = ACTIVE
		}
	default:
		if _, ok := caseData[paramName]; !ok {
			fmt.Printf("[WARNING] 用例表缺少字段: %s\n", paramName)
		}
		result = caseData[paramName]
	}
	if debugFlag {
		fmt.Printf("[参数获取debug] paramName: %s, caseData原始值: %v, 返回值: %+v, 类型: %T\n", paramName, caseData[paramName], result, result)
	}
	return result
}

func TestCreateUser(t *testing.T) {
	ut := &UserTest{}
	err := ut.LoadTestCases("user_cases.xlsx", "user_test.go")
	if err != nil {
		t.Fatalf("用例加载失败: %v", err)
	}
	caseNames := []string{"正常创建用户", "邮箱为空异常", "密码为空异常", "邮箱格式错误", "密码长度不足"}
	ut.Execute(t, ut, caseNames, CreateUser)
}

func TestUpdateProfile(t *testing.T) {
	ut := &UserTest{}
	err := ut.LoadTestCases("user_cases.xlsx", "user_test.go")
	if err != nil {
		t.Fatalf("用例加载失败: %v", err)
	}
	user := &User{}
	caseNames := []string{"正常更新Profile", "Profile字段为空异常"}
	ut.Execute(t, ut, caseNames, user.UpdateProfile)
}

func TestChangePassword(t *testing.T) {
	ut := &UserTest{}
	err := ut.LoadTestCases("user_cases.xlsx", "user_test.go")
	if err != nil {
		t.Fatalf("用例加载失败: %v", err)
	}
	user := &User{Status: ACTIVE}
	caseNames1 := []string{"正常修改密码", "密码为空异常", "密码长度不足"}
	ut.Execute(t, ut, caseNames1, user.ChangePassword)
	user1 := &User{Status: SUSPENDED}
	caseNames2 := []string{"挂起状态下修改密码抛异常"}
	ut.Execute(t, ut, caseNames2, user1.ChangePassword)
}

func TestSuspend(t *testing.T) {
	ut := &UserTest{}
	err := ut.LoadTestCases("user_cases.xlsx", "user_test.go")
	if err != nil {
		t.Fatalf("用例加载失败: %v", err)
	}
	user := &User{Status: ACTIVE}
	caseNames := []string{"正常挂起用户"}
	ut.Execute(t, ut, caseNames, user.Suspend)
}

func TestActivate(t *testing.T) {
	ut := &UserTest{}
	err := ut.LoadTestCases("user_cases.xlsx")
	if err != nil {
		t.Fatalf("用例加载失败: %v", err)
	}
	user := &User{Status: SUSPENDED}
	caseNames := []string{"正常激活用户"}
	ut.Execute(t, ut, caseNames, user.Activate)
}
