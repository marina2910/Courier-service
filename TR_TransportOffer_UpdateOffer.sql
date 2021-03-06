USE [zm180125-tsql]
GO
/****** Object:  Trigger [dbo].[TR_TrasnportOffer_UpdateOffer]    Script Date: 6/2/2022 2:44:12 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================


create TRIGGER [dbo].[TR_TrasnportOffer_UpdateOffer]
   ON  [dbo].[Zahtev isporuke paketa]
   FOR UPDATE
AS 
BEGIN
	
	declare @OsnovnaCena decimal(10,3)
	declare @CenaPoKg decimal(10,3)
	declare @CenaIsporuke decimal(10,3)

	declare @kursor cursor
	declare  @IdAPosiljaoc integer, @IdAPrimaoc integer
	declare @IdZ integer, @Tip integer, @Tezina decimal(10,3)
	
	declare @distanca decimal(10,3)
	declare @XPosiljaoc integer, @YPosiljaoc integer
	declare @XPrimaoc integer, @YPrimaoc integer


	set @kursor = cursor for
	select IdZ, [Adresa posiljaoca], [Adresa primaoca], [Tip paketa], [Tezina paketa]
	from inserted

	open @kursor

	fetch from @kursor
	into @IdZ, @IdAPosiljaoc, @IdAPrimaoc, @Tip, @Tezina

	while @@FETCH_STATUS=0
	begin
		
		if(@Tip=0)
		begin
			set @OsnovnaCena=115
			set @CenaPoKg=0
		end

		else if(@Tip=1)
		begin
			set @OsnovnaCena=175
			set @CenaPoKg=100
		end

		else if(@Tip=2)
		begin
			set @OsnovnaCena=250
			set @CenaPoKg=100
		end
		else
		begin
			set @OsnovnaCena=350
			set @CenaPoKg=500
		end

		select @XPosiljaoc=X, @YPosiljaoc=Y
		from Adresa
		where IdA=@IdAPosiljaoc

		select @XPrimaoc=X, @YPrimaoc=Y
		from Adresa
		where IdA=@IdAPrimaoc

		set @distanca=SQRT(POWER(@XPrimaoc-@XPosiljaoc,2)+POWER(@YPrimaoc-@YPosiljaoc,2))

		set @CenaIsporuke= ( @OsnovnaCena + (@Tezina)*@CenaPoKg ) * @distanca

		UPDATE Ponuda SET [Cena] = @CenaIsporuke
		WHERE [IdZ] = @IdZ

		fetch from @kursor
		into @IdZ, @IdAPosiljaoc, @IdAPrimaoc, @Tip, @Tezina
	end

	close @kursor
	deallocate @kursor

END
