USE [discordDB]
GO

/****** Object:  Table [dbo].[tbl_joined_guild_info]    Script Date: 2023-08-10 2:58:36 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[tbl_joined_guild_info](
	[ID] [varchar](255) NOT NULL,
	[server_name] [varchar](255) NOT NULL,
	[prefix] [varchar](1) NOT NULL,
	[premium_tier] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Guild ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbl_joined_guild_info', @level2type=N'COLUMN',@level2name=N'ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Guild''s name' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbl_joined_guild_info', @level2type=N'COLUMN',@level2name=N'server_name'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Guild''s prefix' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbl_joined_guild_info', @level2type=N'COLUMN',@level2name=N'prefix'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Guild''s premium type' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbl_joined_guild_info', @level2type=N'COLUMN',@level2name=N'premium_tier'
GO


